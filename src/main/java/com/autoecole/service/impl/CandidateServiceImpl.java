package com.autoecole.service.impl;

import java.time.LocalDate;

import com.autoecole.dto.response.CandidateDetailsDTO;
import com.autoecole.dto.response.CandidateListDTO;
import com.autoecole.dto.request.CandidateSearchDTO;
import com.autoecole.exception.BusinessException;
import com.autoecole.exception.NotFoundException;
import com.autoecole.mapper.CandidateMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.autoecole.model.Candidate;
import com.autoecole.repository.CandidateDao;
import com.autoecole.service.CandidateService;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class CandidateServiceImpl implements CandidateService {

	private final CandidateDao candidateDao;
	private final CandidateMapper candidateMapper;

	@Override
	public Candidate saveCandidate(Candidate candidate) {
		log.debug("Attempting to save candidate with CIN: {}", candidate.getCin());

		try {
			// Validate required fields
			validateCandidateForCreation(candidate);

			// Check if candidate already exists
			if (candidateDao.findByCin(candidate.getCin()).isPresent()) {
				throw new BusinessException("Candidate with CIN " + candidate.getCin() + " already exists");
			}

			// Set default values
			candidate.setStartingDate(LocalDate.now());
			if (candidate.getIsActive() == null) {
				candidate.setIsActive(true);
			}

			Candidate savedCandidate = candidateDao.save(candidate);
			log.info("Successfully saved candidate with CIN: {}", savedCandidate.getCin());
			return savedCandidate;

		} catch (DataIntegrityViolationException e) {
			log.error("Data integrity violation while saving candidate with CIN: {}", candidate.getCin(), e);

			// Handle specific constraint violations
			String message = e.getMessage();
			if (message != null && message.contains("uk_candidate_email")) {
				throw new BusinessException("Email address is already in use");
			} else if (message != null && message.contains("PRIMARY")) {
				throw new BusinessException("Candidate with CIN " + candidate.getCin() + " already exists");
			} else {
				throw new BusinessException("Cannot save candidate due to data constraint violation");
			}

		} catch (DataAccessException e) {
			log.error("Database error while saving candidate with CIN: {}", candidate.getCin(), e);
			throw new BusinessException("Database error occurred while saving candidate");

		} catch (Exception e) {
			log.error("Unexpected error while saving candidate with CIN: {}", candidate.getCin(), e);
			throw new BusinessException("An unexpected error occurred while saving candidate");
		}
	}

	@Override
	public Long deleteCandidate(String cin) {
		log.debug("Attempting to delete candidate with CIN: {}", cin);

		try {
			// Validate input
			validateCin(cin);

			// Check if a candidate exists
			Candidate candidate = candidateDao.findByCin(cin)
					.orElseThrow(() -> new NotFoundException("Candidate not found with CIN: " + cin));

			// Check if a candidate has active application files
			if (hasActiveApplicationFiles(candidate)) {
				throw new BusinessException("Cannot delete candidate with active application files. Please cancel or complete all application files first.");
			}

			Long deletedCount = candidateDao.removeByCin(cin);

			if (deletedCount == 0) {
				throw new NotFoundException("Candidate not found with CIN: " + cin);
			}

			log.info("Successfully deleted candidate with CIN: {}", cin);
			return deletedCount;

		} catch (DataIntegrityViolationException e) {
			log.error("Cannot delete candidate due to foreign key constraints for CIN: {}", cin, e);
			throw new BusinessException("Cannot delete candidate: The candidate has related records (sessions, application files, etc.). Please remove related records first.");

		} catch (DataAccessException e) {
			log.error("Database error while deleting candidate with CIN: {}", cin, e);
			throw new BusinessException("Database error occurred while deleting candidate");

		} catch (BusinessException e) {
			throw e; // Re-throw business exceptions as-is

		} catch (Exception e) {
			log.error("Unexpected error while deleting candidate with CIN: {}", cin, e);
			throw new BusinessException("An unexpected error occurred while deleting candidate");
		}
	}

	@Override
	public Candidate findByCin(String cin) {
		log.debug("Searching for candidate with CIN: {}", cin);

		validateCin(cin);

		return candidateDao.findByCin(cin)
				.orElseThrow(() -> new NotFoundException("Candidate not found with CIN: " + cin));
	}

	@Override
	public Candidate updateCandidate(String cin, Candidate candidateUpdates) {
		log.debug("Attempting to update candidate with CIN: {}", cin);

		try {
			// Validate input
			validateCin(cin);
			validateCandidateForUpdate(candidateUpdates);

			// Find existing candidate
			Candidate existingCandidate = candidateDao.findByCin(cin)
					.orElseThrow(() -> new NotFoundException("Candidate not found with CIN: " + cin));

			// Update fields (only non-null values)
			updateCandidateFields(existingCandidate, candidateUpdates);

			Candidate updatedCandidate = candidateDao.save(existingCandidate);
			log.info("Successfully updated candidate with CIN: {}", cin);
			return updatedCandidate;

		} catch (DataIntegrityViolationException e) {
			log.error("Data integrity violation while updating candidate with CIN: {}", cin, e);

			String message = e.getMessage();
			if (message != null && message.contains("uk_candidate_email")) {
				throw new BusinessException("Email address is already in use by another candidate");
			} else {
				throw new BusinessException("Cannot update candidate due to data constraint violation");
			}

		} catch (DataAccessException e) {
			log.error("Database error while updating candidate with CIN: {}", cin, e);
			throw new BusinessException("Database error occurred while updating candidate");

		} catch (BusinessException e) {
			throw e; // Re-throw business exceptions as-is

		} catch (Exception e) {
			log.error("Unexpected error while updating candidate with CIN: {}", cin, e);
			throw new BusinessException("An unexpected error occurred while updating candidate");
		}
	}

	@Override
	public Page<Candidate> searchCandidates(CandidateSearchDTO searchCriteria, Pageable pageable) {
		log.debug("Searching candidates with criteria: {}", searchCriteria);

		try {
			// Validate input
			if (searchCriteria == null) {
				throw new IllegalArgumentException("Search criteria cannot be null");
			}
			if (pageable == null) {
				throw new IllegalArgumentException("Pageable cannot be null");
			}

			// If no search criteria provided, return all candidates
			if (!searchCriteria.hasSearchCriteria()) {
				return candidateDao.findAll(pageable);
			}

			// Prepare search parameters (convert empty strings to null)
			String firstName = isEmptyOrNull(searchCriteria.getFirstName()) ? null : searchCriteria.getFirstName().trim();
			String lastName = isEmptyOrNull(searchCriteria.getLastName()) ? null : searchCriteria.getLastName().trim();
			String cin = isEmptyOrNull(searchCriteria.getCin()) ? null : searchCriteria.getCin().trim();

			return candidateDao.searchCandidates(firstName, lastName, cin, searchCriteria.getIsActive(), pageable);

		} catch (DataAccessException e) {
			log.error("Database error while searching candidates", e);
			throw new BusinessException("Database error occurred while searching candidates");

		} catch (IllegalArgumentException e) {
			throw e; // Re-throw validation errors as-is

		} catch (Exception e) {
			log.error("Unexpected error while searching candidates", e);
			throw new BusinessException("An unexpected error occurred while searching candidates");
		}
	}

	@Override
	public Page<CandidateListDTO> findActiveCandidatesDTO(Boolean isActive, Pageable pageable) {
		log.debug("Finding candidates with active status: {}", isActive);

		try {
			if (pageable == null) {
				throw new IllegalArgumentException("Pageable cannot be null");
			}

			Page<Candidate> candidates = candidateDao.findByIsActive(isActive, pageable);
			return candidateMapper.toListDTOPage(candidates);

		} catch (DataAccessException e) {
			log.error("Database error while finding active candidates", e);
			throw new BusinessException("Database error occurred while finding candidates");

		} catch (IllegalArgumentException e) {
			throw e; // Re-throw validation errors as-is

		} catch (Exception e) {
			log.error("Unexpected error while finding active candidates", e);
			throw new BusinessException("An unexpected error occurred while finding candidates");
		}
	}

	@Override
	public Page<CandidateListDTO> searchCandidatesDTO(CandidateSearchDTO searchCriteria, Pageable pageable) {
		log.debug("Searching candidates DTO with criteria: {}", searchCriteria);

		try {
			Page<Candidate> candidates = searchCandidates(searchCriteria, pageable);
			return candidateMapper.toListDTOPage(candidates);

		} catch (Exception e) {
			// Exception handling is already done in searchCandidates method
			throw e;
		}
	}

	@Override
	public CandidateDetailsDTO getCandidateDetails(String cin) {
		log.debug("Getting candidate details for CIN: {}", cin);

		try {
			validateCin(cin);

			Candidate candidate = candidateDao.findByCin(cin)
					.orElseThrow(() -> new NotFoundException("Candidate not found with CIN: " + cin));

			return CandidateDetailsDTO.fromEntity(candidate);

		} catch (DataAccessException e) {
			log.error("Database error while getting candidate details for CIN: {}", cin, e);
			throw new BusinessException("Database error occurred while retrieving candidate details");

		} catch (NotFoundException e) {
			throw e; // Re-throw as-is

		} catch (Exception e) {
			log.error("Unexpected error while getting candidate details for CIN: {}", cin, e);
			throw new BusinessException("An unexpected error occurred while retrieving candidate details");
		}
	}

	// ==================== PRIVATE VALIDATION METHODS ====================

	private void validateCandidateForCreation(Candidate candidate) {
		if (candidate == null) {
			throw new IllegalArgumentException("Candidate cannot be null");
		}

		validateCin(candidate.getCin());

		if (isEmptyOrNull(candidate.getFirstName())) {
			throw new IllegalArgumentException("First name is required");
		}

		if (isEmptyOrNull(candidate.getLastName())) {
			throw new IllegalArgumentException("Last name is required");
		}

		if (candidate.getFirstName().length() > 100) {
			throw new IllegalArgumentException("First name cannot exceed 100 characters");
		}

		if (candidate.getLastName().length() > 100) {
			throw new IllegalArgumentException("Last name cannot exceed 100 characters");
		}

		// Validate email format if provided
		if (!isEmptyOrNull(candidate.getEmail()) && !isValidEmail(candidate.getEmail())) {
			throw new IllegalArgumentException("Invalid email format");
		}

		// Validate a GSM format if provided
		if (!isEmptyOrNull(candidate.getGsm()) && !isValidGsm(candidate.getGsm())) {
			throw new IllegalArgumentException("Invalid GSM format. Use format: +212XXXXXXXXX or 06XXXXXXXX");
		}
	}

	private void validateCandidateForUpdate(Candidate candidate) {
		if (candidate == null) {
			throw new IllegalArgumentException("Candidate updates cannot be null");
		}

		// Validate fields only if they are provided (not null)
		if (candidate.getFirstName() != null && candidate.getFirstName().length() > 100) {
			throw new IllegalArgumentException("First name cannot exceed 100 characters");
		}

		if (candidate.getLastName() != null && candidate.getLastName().length() > 100) {
			throw new IllegalArgumentException("Last name cannot exceed 100 characters");
		}

		if (!isEmptyOrNull(candidate.getEmail()) && !isValidEmail(candidate.getEmail())) {
			throw new IllegalArgumentException("Invalid email format");
		}

		if (!isEmptyOrNull(candidate.getGsm()) && !isValidGsm(candidate.getGsm())) {
			throw new IllegalArgumentException("Invalid GSM format. Use format: +212XXXXXXXXX or 06XXXXXXXX");
		}
	}

	private void validateCin(String cin) {
		if (isEmptyOrNull(cin)) {
			throw new IllegalArgumentException("CIN is required");
		}

		if (cin.length() > 20) {
			throw new IllegalArgumentException("CIN cannot exceed 20 characters");
		}
	}

	private void updateCandidateFields(Candidate existingCandidate, Candidate candidateUpdates) {
		if (candidateUpdates.getFirstName() != null) {
			existingCandidate.setFirstName(candidateUpdates.getFirstName());
		}
		if (candidateUpdates.getLastName() != null) {
			existingCandidate.setLastName(candidateUpdates.getLastName());
		}
		if (candidateUpdates.getAddress() != null) {
			existingCandidate.setAddress(candidateUpdates.getAddress());
		}
		if (candidateUpdates.getCity() != null) {
			existingCandidate.setCity(candidateUpdates.getCity());
		}
		if (candidateUpdates.getEmail() != null) {
			existingCandidate.setEmail(candidateUpdates.getEmail());
		}
		if (candidateUpdates.getGsm() != null) {
			existingCandidate.setGsm(candidateUpdates.getGsm());
		}
		if (candidateUpdates.getGender() != null) {
			existingCandidate.setGender(candidateUpdates.getGender());
		}
		if (candidateUpdates.getBirthDay() != null) {
			existingCandidate.setBirthDay(candidateUpdates.getBirthDay());
		}
		if (candidateUpdates.getBirthPlace() != null) {
			existingCandidate.setBirthPlace(candidateUpdates.getBirthPlace());
		}
		if (candidateUpdates.getIsActive() != null) {
			existingCandidate.setIsActive(candidateUpdates.getIsActive());
		}
		// Note: startingDate is typically not updated after creation
	}

	private boolean hasActiveApplicationFiles(Candidate candidate) {
		// Check if candidate has any active application files
		return candidate.getApplicationFiles() != null &&
				candidate.getApplicationFiles().stream()
						.anyMatch(appFile -> appFile.getIsActive() != null && appFile.getIsActive());
	}

	// ==================== HELPER VALIDATION METHODS ====================

	private boolean isEmptyOrNull(String str) {
		return str == null || str.trim().isEmpty();
	}

	private boolean isValidEmail(String email) {
		if (isEmptyOrNull(email)) {
			return false;
		}
		// Basic email validation pattern
		String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
		return email.matches(emailPattern);
	}

	private boolean isValidGsm(String gsm) {
		if (isEmptyOrNull(gsm)) {
			return false;
		}
		// Moroccan phone number patterns: +212XXXXXXXXX or 06XXXXXXXX or 07XXXXXXXX
		String morrocanPattern = "^(\\+212[5-7]\\d{8}|0[5-7]\\d{8})$";
		return gsm.replaceAll("\\s", "").matches(morrocanPattern);
	}

	@Override
	public Long countActiveCandidates() {
		return candidateDao.countByIsActive(Boolean.TRUE);
	}
}
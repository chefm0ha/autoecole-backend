package com.springBoot.autoEcole.service.impl;

import java.time.LocalDate;

import com.springBoot.autoEcole.dto.CandidateListDTO;
import com.springBoot.autoEcole.dto.CandidateSearchDTO;
import com.springBoot.autoEcole.mapper.CandidateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.springBoot.autoEcole.model.Candidate;
import com.springBoot.autoEcole.repository.CandidateDao;
import com.springBoot.autoEcole.service.CandidateService;

@Service
@Transactional
public class CandidateServiceImpl implements CandidateService {

	@Autowired
	private CandidateDao candidateDao;

	@Autowired
	private CandidateMapper candidateMapper;

	@Override
	public Candidate saveCandidate(Candidate candidate) {
		candidate.setStartingDate(LocalDate.now());
		return candidateDao.save(candidate);
	}

	@Override
	public Long deleteCandidate(String cin) {
		return candidateDao.removeByCin(cin);
	}

	@Override
	public Candidate findByCin(String cin) {
		return candidateDao.findByCin(cin).orElse(null);
	}

	@Override
	public Candidate updateCandidate(String cin, Candidate candidateUpdates) {
		// Find existing candidate
		Candidate existingCandidate = candidateDao.findByCin(cin).orElse(null);

		if (existingCandidate == null) {
			return null; // or throw exception
		}

		// Update fields (only non-null values)
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

		return candidateDao.save(existingCandidate);
	}

	@Override
	public Page<Candidate> searchCandidates(CandidateSearchDTO searchCriteria, Pageable pageable) {
		// If no search criteria provided, return all candidates
		if (!searchCriteria.hasSearchCriteria()) {
			return candidateDao.findAll(pageable);
		}

		// Prepare search parameters (convert empty strings to null)
		String firstName = isEmptyOrNull(searchCriteria.getFirstName()) ? null : searchCriteria.getFirstName().trim();
		String lastName = isEmptyOrNull(searchCriteria.getLastName()) ? null : searchCriteria.getLastName().trim();
		String cin = isEmptyOrNull(searchCriteria.getCin()) ? null : searchCriteria.getCin().trim();

		return candidateDao.searchCandidates(
				firstName,
				lastName,
				cin,
				searchCriteria.getIsActive(),
				pageable
		);
	}

	@Override
	public Page<CandidateListDTO> findActiveCandidatesDTO(Boolean isActive, Pageable pageable) {
		Page<Candidate> candidates = candidateDao.findByIsActive(isActive, pageable);
		return candidateMapper.toListDTOPage(candidates);
	}

	@Override
	public Page<CandidateListDTO> searchCandidatesDTO(CandidateSearchDTO searchCriteria, Pageable pageable) {
		Page<Candidate> candidates = searchCandidates(searchCriteria, pageable);
		return candidateMapper.toListDTOPage(candidates);
	}

	// Helper method to check if string is null or empty
	private boolean isEmptyOrNull(String str) {
		return str == null || str.trim().isEmpty();
	}
}
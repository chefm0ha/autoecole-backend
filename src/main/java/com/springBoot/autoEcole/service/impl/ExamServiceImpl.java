package com.springBoot.autoEcole.service.impl;

import com.springBoot.autoEcole.dto.ExamRequestDTO;
import com.springBoot.autoEcole.dto.ExamResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.springBoot.autoEcole.model.ApplicationFile;
import com.springBoot.autoEcole.model.Exam;
import com.springBoot.autoEcole.repository.ExamDao;
import com.springBoot.autoEcole.service.ApplicationFileService;
import com.springBoot.autoEcole.service.ExamService;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ExamServiceImpl implements ExamService {

	@Autowired
	private ApplicationFileService applicationFileService;

	@Autowired
	private ExamDao examDao;

	@Override
	public Exam saveExam(Long applicationFileId, ExamRequestDTO examRequest) {
		// Validate application file
		ApplicationFile applicationFile = validateApplicationFile(applicationFileId);

		// Call stored procedure to save exam with business logic
		try {
			examDao.saveExamWithBusinessLogic(
					applicationFileId,
					examRequest.getExamType(),
					examRequest.getDate(),
					examRequest.getStatus()
			);

			return examDao.findLatestExamByApplicationFileAndType(applicationFileId, examRequest.getExamType());

		} catch (Exception e) {
			String errorMessage = extractErrorMessage(e);
			throw new IllegalStateException(errorMessage);
		}
	}

	@Override
	public List<ExamResponseDTO> getExamsByApplicationFile(Long applicationFileId) {
		// Validate application file exists (but don't check if active - we should be able to view exams for inactive files)
		ApplicationFile applicationFile = validateApplicationFileExists(applicationFileId);

		List<Exam> exams = examDao.findByApplicationFileOrderByDateDesc(applicationFile);
		return exams.stream()
				.map(ExamResponseDTO::fromEntity)
				.collect(Collectors.toList());
	}

	@Override
	public Exam updateExamStatus(Long examId, String newStatus) {
		// Validate exam exists
		Exam existingExam = validateExamExists(examId);

		// Validate and normalize status
		String normalizedStatus = validateAndNormalizeStatus(newStatus);

		// Check business rules
		validateStatusChangeRules(existingExam, normalizedStatus);

		try {
			// Call stored procedure to update exam status with business logic
			examDao.updateExamStatusWithBusinessLogic(examId, normalizedStatus);

			// Return the updated exam
			return examDao.findById(examId).orElse(existingExam);

		} catch (Exception e) {
			String errorMessage = extractErrorMessage(e);
			throw new IllegalStateException(errorMessage);
		}
	}

	// ==================== PRIVATE VALIDATION METHODS ====================

	private ApplicationFile validateApplicationFile(Long applicationFileId) {
		ApplicationFile applicationFile = applicationFileService.findById(applicationFileId);
		if (applicationFile == null) {
			throw new EntityNotFoundException("Application file not found with ID: " + applicationFileId);
		}

		if (!applicationFile.getIsActive()) {
			throw new IllegalStateException("Cannot add exam to inactive application file");
		}

		return applicationFile;
	}

	private ApplicationFile validateApplicationFileExists(Long applicationFileId) {
		ApplicationFile applicationFile = applicationFileService.findById(applicationFileId);
		if (applicationFile == null) {
			throw new EntityNotFoundException("Application file not found with ID: " + applicationFileId);
		}

		return applicationFile;
	}

	private Exam validateExamExists(Long examId) {
		Exam exam = examDao.findById(examId).orElse(null);
		if (exam == null) {
			throw new EntityNotFoundException("Exam not found with ID: " + examId);
		}
		return exam;
	}

	private String validateAndNormalizeStatus(String newStatus) {
		if (newStatus == null || newStatus.trim().isEmpty()) {
			throw new IllegalArgumentException("Status cannot be null or empty");
		}

		String normalizedStatus = newStatus.trim().toUpperCase();
		if (!isValidStatus(normalizedStatus)) {
			throw new IllegalArgumentException("Invalid status. Allowed values: SCHEDULED, PASSED, FAILED");
		}

		return normalizedStatus;
	}

	private boolean isValidStatus(String status) {
		return status.equals("SCHEDULED") ||
				status.equals("PASSED") ||
				status.equals("FAILED");
	}

	private void validateStatusChangeRules(Exam existingExam, String newStatus) {
		// Business rule: Cannot change status of an already passed exam
		if (existingExam.getStatus().equals("PASSED") && !newStatus.equals("PASSED")) {
			throw new IllegalStateException("Cannot change status of an already passed exam");
		}
	}

	// ==================== ERROR MESSAGE EXTRACTION ====================

	private String extractErrorMessage(Exception e) {
		String message = e.getMessage();
		if (message == null) {
			return "Database error occurred";
		}

		// Try to extract the actual error message from nested exceptions
		Throwable cause = e;
		while (cause != null) {
			String causeMessage = cause.getMessage();
			if (causeMessage != null) {
				String extractedMessage = mapKnownErrorMessage(causeMessage);
				if (extractedMessage != null) {
					return extractedMessage;
				}
			}
			cause = cause.getCause();
		}

		return message;
	}

	private String mapKnownErrorMessage(String causeMessage) {
		// Map known database error messages to user-friendly messages
		if (causeMessage.contains("Maximum number of attempts (2) exceeded for theory exam")) {
			return "Maximum number of attempts (2) exceeded for theory exam";
		}
		if (causeMessage.contains("Maximum number of attempts (3) exceeded for practical exam")) {
			return "Maximum number of attempts (3) exceeded for practical exam";
		}
		if (causeMessage.contains("Maximum number of attempts")) {
			return "Maximum number of attempts exceeded for this exam type";
		}
		if (causeMessage.contains("Cannot schedule theory exam")) {
			return "Cannot schedule theory exam: there is already a scheduled theory exam. Complete the current exam first.";
		}
		if (causeMessage.contains("Cannot schedule practical exam: there is already a scheduled practical exam")) {
			return "Cannot schedule practical exam: there is already a scheduled practical exam. Complete the current exam first.";
		}
		if (causeMessage.contains("Cannot schedule practical exam: theory exam must be passed first")) {
			return "Cannot schedule practical exam: theory exam must be passed first.";
		}
		if (causeMessage.contains("Cannot add another THEORY exam")) {
			return "Cannot add another THEORY exam: already have a PASSED exam of this type";
		}
		if (causeMessage.contains("Cannot add another PRACTICAL exam")) {
			return "Cannot add another PRACTICAL exam: already have a PASSED exam of this type";
		}
		if (causeMessage.contains("Tax stamp must be PAID")) {
			return "Cannot schedule exam: Tax stamp must be PAID first";
		}
		if (causeMessage.contains("Medical visit must be COMPLETED")) {
			return "Cannot schedule exam: Medical visit must be COMPLETED first";
		}

		return null; // No known mapping found
	}
}
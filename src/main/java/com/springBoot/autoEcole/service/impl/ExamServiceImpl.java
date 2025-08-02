package com.springBoot.autoEcole.service.impl;

import com.springBoot.autoEcole.dto.CalendarExamDTO;
import com.springBoot.autoEcole.dto.ExamRequestDTO;
import com.springBoot.autoEcole.dto.ExamResponseDTO;
import com.springBoot.autoEcole.enums.ApplicationFileStatus;
import com.springBoot.autoEcole.enums.ExamStatus;
import com.springBoot.autoEcole.enums.ExamType;
import com.springBoot.autoEcole.enums.MedicalVisitStatus;
import com.springBoot.autoEcole.enums.TaxStampStatus;
import com.springBoot.autoEcole.mapper.ExamMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.springBoot.autoEcole.model.ApplicationFile;
import com.springBoot.autoEcole.model.Exam;
import com.springBoot.autoEcole.repository.ExamDao;
import com.springBoot.autoEcole.service.ApplicationFileService;
import com.springBoot.autoEcole.service.ExamService;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ExamServiceImpl implements ExamService {

	@Autowired
	private ApplicationFileService applicationFileService;

	@Autowired
	private ExamDao examDao;

	@Autowired
	private ExamMapper examMapper;

	@Override
	public Exam saveExam(Long applicationFileId, ExamRequestDTO examRequest) {
		// 1. Validate application file exists and is active
		ApplicationFile applicationFile = validateApplicationFile(applicationFileId);

		// 2. Validate prerequisites (tax stamp, medical visit)
		validateExamPrerequisites(applicationFile);

		// 3. Parse and validate exam type and status
		ExamType examType = ExamType.valueOf(examRequest.getExamType());
		ExamStatus examStatus = ExamStatus.valueOf(examRequest.getStatus());

		// 4. Check attempt limits and existing exams
		validateExamBusinessRules(applicationFile, examType, examStatus);

		// 5. For practical exams, ensure theory is passed
		if (examType == ExamType.PRACTICAL) {
			validateTheoryExamPassed(applicationFile);
		}

		// 6. Calculate attempt number
		Integer attemptNumber = calculateAttemptNumber(applicationFile, examType);

		// 7. Create source exam with the data
		Exam sourceExam = new Exam();
		sourceExam.setExamType(examType);
		sourceExam.setStatus(examStatus);
		sourceExam.setDate(examRequest.getDate());
		sourceExam.setAttemptNumber(attemptNumber);

		// 8. Create and save exam using mapper
		Exam exam = examMapper.toEntity(sourceExam, applicationFile);
		Exam savedExam = examDao.save(exam);

		// 9. Update application file status based on exam
		updateApplicationFileStatusAfterExam(applicationFile, examType, examStatus);

		return savedExam;
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
		// 1. Validate exam exists
		Exam exam = validateExamExists(examId);

		// 2. Validate and parse new status
		ExamStatus newExamStatus = ExamStatus.valueOf(newStatus.toUpperCase());

		// 3. Validate status transition rules
		validateStatusTransition(exam, newExamStatus);

		// 4. Update exam
		ExamStatus oldStatus = exam.getStatus();
		exam.setStatus(newExamStatus);
		Exam updatedExam = examDao.save(exam);

		// 5. Update application file status based on new exam status
		updateApplicationFileStatusAfterStatusChange(exam.getApplicationFile(), exam.getExamType(), oldStatus, newExamStatus);

		return updatedExam;
	}

	// ==================== CALENDAR METHODS ====================

	@Override
	public List<CalendarExamDTO> getExamsByMonth(int year, int month) {
		// Validate month range
		if (month < 1 || month > 12) {
			throw new IllegalArgumentException("Month must be between 1 and 12");
		}

		// Validate year range (reasonable bounds)
		if (year < 2000 || year > 2100) {
			throw new IllegalArgumentException("Year must be between 2000 and 2100");
		}

		List<Exam> exams = examDao.findExamsByYearAndMonth(year, month);
		return exams.stream()
				.map(CalendarExamDTO::fromEntity)
				.collect(Collectors.toList());
	}

	@Override
	public List<CalendarExamDTO> getExamsByDate(LocalDate date) {
		if (date == null) {
			throw new IllegalArgumentException("Date cannot be null");
		}

		List<Exam> exams = examDao.findExamsByDate(date);
		return exams.stream()
				.map(CalendarExamDTO::fromEntity)
				.collect(Collectors.toList());
	}

	@Override
	public List<CalendarExamDTO> getExamsByDateRange(LocalDate startDate, LocalDate endDate) {
		if (startDate == null || endDate == null) {
			throw new IllegalArgumentException("Start date and end date cannot be null");
		}

		if (startDate.isAfter(endDate)) {
			throw new IllegalArgumentException("Start date cannot be after end date");
		}

		List<Exam> exams = examDao.findExamsByDateRange(startDate, endDate);
		return exams.stream()
				.map(CalendarExamDTO::fromEntity)
				.collect(Collectors.toList());
	}

	@Override
	public List<CalendarExamDTO> getScheduledExamsFromDate(LocalDate fromDate) {
		if (fromDate == null) {
			fromDate = LocalDate.now(); // Default to today
		}

		List<Exam> exams = examDao.findScheduledExamsFromDate(fromDate);
		return exams.stream()
				.map(CalendarExamDTO::fromEntity)
				.collect(Collectors.toList());
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

	// ==================== BUSINESS LOGIC METHODS ====================

	private void validateExamPrerequisites(ApplicationFile applicationFile) {
		if (applicationFile.getTaxStamp() != TaxStampStatus.PAID) {
			throw new IllegalStateException("Tax stamp must be PAID");
		}

		if (applicationFile.getMedicalVisit() != MedicalVisitStatus.COMPLETED) {
			throw new IllegalStateException("Medical visit must be COMPLETED");
		}
	}

	private void validateExamBusinessRules(ApplicationFile applicationFile, ExamType examType, ExamStatus examStatus) {
		List<Exam> existingExams = examDao.findByApplicationFileOrderByDateDesc(applicationFile);

		// Count exams by type and status
		long passedCount = existingExams.stream()
			.filter(e -> e.getExamType() == examType && e.getStatus() == ExamStatus.PASSED)
			.count();

		long scheduledCount = existingExams.stream()
			.filter(e -> e.getExamType() == examType && e.getStatus() == ExamStatus.SCHEDULED)
			.count();

		long failedCount = existingExams.stream()
			.filter(e -> e.getExamType() == examType && e.getStatus() == ExamStatus.FAILED)
			.count();

		long totalFailedCount = existingExams.stream()
			.filter(e -> e.getStatus() == ExamStatus.FAILED)
			.count();

		// Business rules validation
		if (passedCount > 0) {
			throw new IllegalStateException("Cannot add another " + examType + " exam: already have a PASSED exam of this type");
		}

		if (examStatus == ExamStatus.SCHEDULED && scheduledCount > 0) {
			throw new IllegalStateException("Cannot schedule " + examType.name().toLowerCase() + " exam: there is already a scheduled " + examType.name().toLowerCase() + " exam");
		}

		// Check total failure limit (max 2 failures across all exam types)
		if (examStatus == ExamStatus.FAILED && totalFailedCount >= 2) {
			throw new IllegalStateException("Cannot add more exams: application file has already failed due to multiple failures");
		}
	}

	private void validateTheoryExamPassed(ApplicationFile applicationFile) {
		List<Exam> exams = examDao.findByApplicationFileOrderByDateDesc(applicationFile);
		boolean theoryPassed = exams.stream()
			.anyMatch(e -> e.getExamType() == ExamType.THEORY && e.getStatus() == ExamStatus.PASSED);

		if (!theoryPassed) {
			throw new IllegalStateException("Cannot schedule practical exam: theory exam must be passed first");
		}
	}

	private Integer calculateAttemptNumber(ApplicationFile applicationFile, ExamType examType) {
		List<Exam> existingExams = examDao.findByApplicationFileOrderByDateDesc(applicationFile);
		int existingAttempts = (int) existingExams.stream()
			.filter(e -> e.getExamType() == examType)
			.count();
		return existingAttempts + 1;
	}

	private void updateApplicationFileStatusAfterExam(ApplicationFile applicationFile, ExamType examType, ExamStatus examStatus) {
		if (examStatus == ExamStatus.SCHEDULED) {
			if (examType == ExamType.THEORY) {
				applicationFile.setStatus(ApplicationFileStatus.THEORY_EXAM_SCHEDULED);
			} else if (examType == ExamType.PRACTICAL) {
				applicationFile.setStatus(ApplicationFileStatus.PRACTICAL_EXAM_SCHEDULED);
			}
		}
		// Other status updates will be handled by the trigger replacement logic
		updateApplicationFileStatusBasedOnExams(applicationFile);
	}

	private void validateStatusTransition(Exam exam, ExamStatus newStatus) {
		// Cannot change status of already passed exam to non-passed
		if (exam.getStatus() == ExamStatus.PASSED && newStatus != ExamStatus.PASSED) {
			throw new IllegalStateException("Cannot change status of an already passed exam");
		}
	}

	private void updateApplicationFileStatusAfterStatusChange(ApplicationFile applicationFile, ExamType examType, ExamStatus oldStatus, ExamStatus newStatus) {
		// If marking as PASSED, check if both exams are now passed
		if (newStatus == ExamStatus.PASSED) {
			updateApplicationFileStatusBasedOnExams(applicationFile);
		}
		// If changing from PASSED to FAILED, revert to IN_PROGRESS
		else if (oldStatus == ExamStatus.PASSED && newStatus == ExamStatus.FAILED) {
			applicationFile.setStatus(ApplicationFileStatus.IN_PROGRESS);
			applicationFileService.updateApplicationFile(applicationFile.getId(), applicationFile);
		}
	}

	/**
	 * Updates application file status based on current exam states (replaces trigger logic)
	 */
	private void updateApplicationFileStatusBasedOnExams(ApplicationFile applicationFile) {
		List<Exam> exams = examDao.findByApplicationFileOrderByDateDesc(applicationFile);
		
		// Count exam statuses
		long theoryPassed = exams.stream().filter(e -> e.getExamType() == ExamType.THEORY && e.getStatus() == ExamStatus.PASSED).count();
		long theoryFailed = exams.stream().filter(e -> e.getExamType() == ExamType.THEORY && e.getStatus() == ExamStatus.FAILED).count();
		long theoryScheduled = exams.stream().filter(e -> e.getExamType() == ExamType.THEORY && e.getStatus() == ExamStatus.SCHEDULED).count();
		
		long practicalPassed = exams.stream().filter(e -> e.getExamType() == ExamType.PRACTICAL && e.getStatus() == ExamStatus.PASSED).count();
		long practicalFailed = exams.stream().filter(e -> e.getExamType() == ExamType.PRACTICAL && e.getStatus() == ExamStatus.FAILED).count();
		long practicalScheduled = exams.stream().filter(e -> e.getExamType() == ExamType.PRACTICAL && e.getStatus() == ExamStatus.SCHEDULED).count();
		
		long totalTheoryAttempts = exams.stream().filter(e -> e.getExamType() == ExamType.THEORY).count();
		long totalPracticalAttempts = exams.stream().filter(e -> e.getExamType() == ExamType.PRACTICAL).count();
		long totalFailed = theoryFailed + practicalFailed;

		ApplicationFileStatus newStatus;

		// Determine new status based on exam states
		if (theoryPassed > 0 && practicalPassed > 0) {
			// Both theory and practical passed - COMPLETED
			newStatus = ApplicationFileStatus.COMPLETED;
			applicationFile.setIsActive(false);
		} else if (totalFailed >= 2) {
			// Two or more failures total - FAILED
			newStatus = ApplicationFileStatus.FAILED;
			applicationFile.setIsActive(false);
		} else if (theoryPassed > 0 && practicalScheduled > 0) {
			// Theory passed, practical scheduled
			newStatus = ApplicationFileStatus.PRACTICAL_EXAM_SCHEDULED;
		} else if (theoryPassed > 0 && practicalFailed > 0) {
			// Theory passed, practical failed
			newStatus = ApplicationFileStatus.PRACTICAL_FAILED;
		} else if (theoryPassed > 0) {
			// Theory passed, no practical attempts yet
			newStatus = ApplicationFileStatus.THEORY_PASSED;
		} else if (theoryScheduled > 0) {
			// Theory exam scheduled
			newStatus = ApplicationFileStatus.THEORY_EXAM_SCHEDULED;
		} else if (theoryFailed > 0 && theoryPassed == 0) {
			// Theory failed but not exhausted attempts
			newStatus = ApplicationFileStatus.THEORY_FAILED;
		} else {
			// Default state - actively learning
			newStatus = ApplicationFileStatus.IN_PROGRESS;
		}

		applicationFile.setStatus(newStatus);
		applicationFileService.updateApplicationFile(applicationFile.getId(), applicationFile);
	}
}
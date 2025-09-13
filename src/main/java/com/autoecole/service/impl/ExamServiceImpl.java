package com.autoecole.service.impl;

import com.autoecole.dto.response.CalendarExamDTO;
import com.autoecole.dto.request.ExamRequestDTO;
import com.autoecole.dto.response.ExamResponseDTO;
import com.autoecole.dto.response.VehicleDTO;
import com.autoecole.enums.*;
import com.autoecole.exception.BusinessException;
import com.autoecole.exception.NotFoundException;
import com.autoecole.mapper.ExamMapper;
import com.autoecole.model.*;
import com.autoecole.repository.ExamDao;
import com.autoecole.repository.VehicleDao;
import com.autoecole.service.ApplicationFileService;
import com.autoecole.service.ExamService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class ExamServiceImpl implements ExamService {

	private final ApplicationFileService applicationFileService;
	private final ExamDao examDao;
	private final VehicleDao vehicleDao;
	private final ExamMapper examMapper;

	@Override
	@Transactional
	public Exam saveExam(Long applicationFileId, ExamRequestDTO examRequest) {
		// 1. Validate application file exists and is active
		ApplicationFile applicationFile = validateApplicationFile(applicationFileId);

		// 2. Validate prerequisites (tax stamp, medical visit)
		validateExamPrerequisites(applicationFile);

		// 3. Parse and validate exam type and status
		ExamType examType = parseExamType(examRequest.getExamType());
		ExamStatus examStatus = parseExamStatus(examRequest.getStatus());

		// 4. Validate exam request data
		validateExamRequest(examRequest);

		// 5. Check attempt limits and existing exams
		validateExamBusinessRules(applicationFile, examType, examStatus);

		// 6. For practical exams, ensure theory is passed
		if (examType == ExamType.PRACTICAL) {
			validateTheoryExamPassed(applicationFile);
		}

		// 7. Calculate attempt number
		Integer attemptNumber = calculateAttemptNumber(applicationFile);

		// 8. Create and save exam
		Exam sourceExam = buildExam(examRequest, examType, examStatus, attemptNumber);
		Exam exam = examMapper.toEntity(sourceExam, applicationFile);
		Exam savedExam = examDao.save(exam);

		// 9. Handle vehicle quota for practical exams
		if (examType == ExamType.PRACTICAL) {
			handleVehicleQuota(examRequest.getImmatriculation());
		}

		// 10. Update application file status
		updateApplicationFileStatusAfterExam(applicationFile, examType, examStatus);

		return savedExam;
	}

	@Override
	public List<ExamResponseDTO> getExamsByApplicationFile(Long applicationFileId) {
		ApplicationFile applicationFile = applicationFileService.findById(applicationFileId);

		List<Exam> exams = examDao.findByApplicationFileOrderByDateDesc(applicationFile);
		return exams.stream()
				.map(ExamResponseDTO::fromEntity)
				.toList();
	}

	@Override
	public List<ExamResponseDTO> getComingExams(int size) {
		PageRequest pageRequest = PageRequest.of(0, size);
		List<Exam> exams = examDao.findByDateAfterOrderByDateAsc(LocalDate.now(), pageRequest);

		return exams.stream()
				.map(ExamResponseDTO::fromEntity)
				.collect(Collectors.toList());
	}

	@Override
	public Exam updateExamStatus(Long examId, String newStatus) {
		// 1. Validate exam exists
		Exam exam = validateExamExists(examId);

		// 2. Parse and validate new status
		ExamStatus newExamStatus = parseExamStatus(newStatus);

		// 3. Validate status transition rules
		validateStatusTransition(exam, newExamStatus);

		// 4. Update exam
		ExamStatus oldStatus = exam.getStatus();
		exam.setStatus(newExamStatus);
		Exam updatedExam = examDao.save(exam);

		// 5. Update application file status
		updateApplicationFileStatusAfterStatusChange(
				exam.getApplicationFile(), oldStatus, newExamStatus);

		return updatedExam;
	}

	// ==================== CALENDAR METHODS ====================

	@Override
	public List<CalendarExamDTO> getExamsByMonth(int year, int month) {
		validateMonthYear(year, month);

		List<Exam> exams = examDao.findExamsByYearAndMonth(year, month);
		return exams.stream()
				.map(CalendarExamDTO::fromEntity)
				.toList();
	}

	@Override
	public List<CalendarExamDTO> getExamsByDate(LocalDate date) {
		if (date == null) {
			throw new IllegalArgumentException("Date cannot be null");
		}

		List<Exam> exams = examDao.findExamsByDate(date);
		return exams.stream()
				.map(CalendarExamDTO::fromEntity)
				.toList();
	}

	@Override
	public List<Exam> getScheduledExamsThisWeek() {
		LocalDate today = LocalDate.now();
		int dayNumber = today.getDayOfWeek().getValue();

		LocalDate startDate = today.minusDays(dayNumber - 1);
		LocalDate endDate = startDate.plusDays(6);

		return examDao.findByDateBetween(startDate, endDate);
	}

	@Override
	public int getPassedExamsByExamType(ExamType examType,LocalDate startDate, LocalDate endDate) {
		validateNotNull(Map.of(
				"examType", examType,
				"startDate", startDate,
				"endDate", endDate
		));

		return examDao.countByExamTypeAndStatusAndDateBetween(
				examType, ExamStatus.PASSED, startDate, endDate
		);
	}

	@Override
	public int getTotalExamsByExamType(ExamType examType, LocalDate startDate, LocalDate endDate) {
		validateNotNull(Map.of(
				"examType", examType,
				"startDate", startDate,
				"endDate", endDate
		));

		return examDao.countAllByExamTypeAndDateBetween(
				examType, startDate, endDate
		);
	}

	// ==================== PRIVATE VALIDATION METHODS ====================
	private void validateNotNull(Map<String, Object> params) {
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			if (entry.getValue() == null) {
				throw new IllegalArgumentException(entry.getKey() + " must not be null");
			}
		}
	}

	private ApplicationFile validateApplicationFile(Long applicationFileId) {
		ApplicationFile applicationFile = applicationFileService.findById(applicationFileId);

		if (Boolean.FALSE.equals(applicationFile.getIsActive())) {
			throw new BusinessException("Cannot add exam to inactive application file");
		}

		return applicationFile;
	}

	private Exam validateExamExists(Long examId) {
		return examDao.findById(examId)
				.orElseThrow(() -> new NotFoundException("Exam not found with ID: " + examId));
	}

	private ExamType parseExamType(String examType) {
		if (examType == null || examType.trim().isEmpty()) {
			throw new IllegalArgumentException("Exam type cannot be null or empty");
		}

		try {
			return ExamType.valueOf(examType.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Invalid exam type: " + examType +
					". Valid values are: THEORY, PRACTICAL");
		}
	}

	private ExamStatus parseExamStatus(String status) {
		if (status == null || status.trim().isEmpty()) {
			throw new IllegalArgumentException("Exam status cannot be null or empty");
		}

		try {
			return ExamStatus.valueOf(status.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Invalid exam status: " + status +
					". Valid values are: SCHEDULED, PASSED, FAILED");
		}
	}

	private void validateExamRequest(ExamRequestDTO examRequest) {
		if (examRequest.getDate() == null) {
			throw new IllegalArgumentException("Exam date cannot be null");
		}

		if (examRequest.getDate().isBefore(LocalDate.now())) {
			throw new IllegalArgumentException("Exam date cannot be in the past");
		}

		// For practical exams, immatriculation is required
		if ("PRACTICAL".equalsIgnoreCase(examRequest.getExamType()) &&
				(examRequest.getImmatriculation() == null || examRequest.getImmatriculation().trim().isEmpty())) {
			throw new IllegalArgumentException("Vehicle immatriculation is required for practical exams");
		}
	}

	private void validateMonthYear(int year, int month) {
		if (month < 1 || month > 12) {
			throw new IllegalArgumentException("Month must be between 1 and 12, but was: " + month);
		}

		if (year < 2000 || year > 2100) {
			throw new IllegalArgumentException("Year must be between 2000 and 2100, but was: " + year);
		}
	}

	private void validateExamPrerequisites(ApplicationFile applicationFile) {
		if (applicationFile.getTaxStamp() != TaxStampStatus.PAID) {
			throw new BusinessException("Cannot schedule exam: Tax stamp must be paid first");
		}

		if (applicationFile.getMedicalVisit() != MedicalVisitStatus.COMPLETED) {
			throw new BusinessException("Cannot schedule exam: Medical visit must be completed first");
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

		long totalFailedCount = existingExams.stream()
				.filter(e -> e.getStatus() == ExamStatus.FAILED)
				.count();

		// Business rules validation
		if (passedCount > 0) {
			throw new BusinessException("Cannot add another " + examType + " exam: already have a PASSED exam of this type");
		}

		if (examStatus == ExamStatus.SCHEDULED && scheduledCount > 0) {
			throw new BusinessException("Cannot schedule " + examType.name().toLowerCase() +
					" exam: there is already a scheduled " + examType.name().toLowerCase() + " exam");
		}

		if (examStatus == ExamStatus.FAILED && totalFailedCount >= 2) {
			throw new BusinessException("Cannot add more exams: application file has already failed due to multiple failures");
		}
	}

	private void validateTheoryExamPassed(ApplicationFile applicationFile) {
		List<Exam> exams = examDao.findByApplicationFileOrderByDateDesc(applicationFile);
		boolean theoryPassed = exams.stream()
				.anyMatch(e -> e.getExamType() == ExamType.THEORY && e.getStatus() == ExamStatus.PASSED);

		if (!theoryPassed) {
			throw new BusinessException("Cannot schedule practical exam: theory exam must be passed first");
		}
	}

	private void validateStatusTransition(Exam exam, ExamStatus newStatus) {
		if (exam.getStatus() == ExamStatus.PASSED && newStatus != ExamStatus.PASSED) {
			throw new BusinessException("Cannot change status of an already passed exam");
		}
	}

	private void handleVehicleQuota(String immatriculation) {
		if (immatriculation == null || immatriculation.trim().isEmpty()) {
			throw new IllegalArgumentException("Vehicle immatriculation is required for practical exams");
		}

		int rows = vehicleDao.decrementQuotaIfPositive(immatriculation);
		if (rows == 0) {
			Vehicle vehicle = vehicleDao.findByImmatriculation(immatriculation);
			if (vehicle == null) {
				throw new NotFoundException("Vehicle with immatriculation " + immatriculation + " not found");
			} else {
				throw new BusinessException("Vehicle has no quota left");
			}
		}
	}

	private Exam buildExam(ExamRequestDTO request, ExamType examType, ExamStatus examStatus, Integer attemptNumber) {
		Exam exam = new Exam();
		exam.setExamType(examType);
		exam.setStatus(examStatus);
		exam.setDate(request.getDate());
		exam.setAttemptNumber(attemptNumber);
		return exam;
	}

	private Integer calculateAttemptNumber(ApplicationFile applicationFile) {
		List<Exam> existingExams = examDao.findByApplicationFileOrderByDateDesc(applicationFile);
		return existingExams.stream()
				.mapToInt(Exam::getAttemptNumber)
				.max()
				.orElse(0) + 1;
	}

	private void updateApplicationFileStatusAfterExam(ApplicationFile applicationFile, ExamType examType, ExamStatus examStatus) {
		if (examStatus == ExamStatus.SCHEDULED) {
			if (examType == ExamType.THEORY) {
				applicationFile.setStatus(ApplicationFileStatus.THEORY_EXAM_SCHEDULED);
			} else if (examType == ExamType.PRACTICAL) {
				applicationFile.setStatus(ApplicationFileStatus.PRACTICAL_EXAM_SCHEDULED);
			}
		}
		updateApplicationFileStatusBasedOnExams(applicationFile);
	}

	private void updateApplicationFileStatusAfterStatusChange(ApplicationFile applicationFile, ExamStatus oldStatus, ExamStatus newStatus) {
		if (newStatus == ExamStatus.PASSED) {
			updateApplicationFileStatusBasedOnExams(applicationFile);
		} else if (oldStatus == ExamStatus.PASSED && newStatus == ExamStatus.FAILED) {
			applicationFile.setStatus(ApplicationFileStatus.IN_PROGRESS);
			applicationFileService.updateApplicationFile(applicationFile.getId(), applicationFile);
		}
	}

	private void updateApplicationFileStatusBasedOnExams(ApplicationFile applicationFile) {
		List<Exam> exams = examDao.findByApplicationFileOrderByDateDesc(applicationFile);

		long theoryPassed = exams.stream().filter(e -> e.getExamType() == ExamType.THEORY && e.getStatus() == ExamStatus.PASSED).count();
		long theoryFailed = exams.stream().filter(e -> e.getExamType() == ExamType.THEORY && e.getStatus() == ExamStatus.FAILED).count();
		long theoryScheduled = exams.stream().filter(e -> e.getExamType() == ExamType.THEORY && e.getStatus() == ExamStatus.SCHEDULED).count();

		long practicalPassed = exams.stream().filter(e -> e.getExamType() == ExamType.PRACTICAL && e.getStatus() == ExamStatus.PASSED).count();
		long practicalFailed = exams.stream().filter(e -> e.getExamType() == ExamType.PRACTICAL && e.getStatus() == ExamStatus.FAILED).count();
		long practicalScheduled = exams.stream().filter(e -> e.getExamType() == ExamType.PRACTICAL && e.getStatus() == ExamStatus.SCHEDULED).count();

		long totalFailed = theoryFailed + practicalFailed;

		ApplicationFileStatus newStatus;

		if (totalFailed >= 2) {
			newStatus = ApplicationFileStatus.FAILED;
			applicationFile.setIsActive(false);
		} else if (theoryPassed > 0 && practicalPassed > 0) {
			newStatus = ApplicationFileStatus.COMPLETED;
			applicationFile.setIsActive(false);
		} else if (theoryPassed > 0 && practicalScheduled > 0) {
			newStatus = ApplicationFileStatus.PRACTICAL_EXAM_SCHEDULED;
		} else if (theoryPassed > 0 && practicalFailed > 0) {
			newStatus = ApplicationFileStatus.PRACTICAL_FAILED;
		} else if (theoryPassed > 0) {
			newStatus = ApplicationFileStatus.THEORY_PASSED;
		} else if (theoryScheduled > 0) {
			newStatus = ApplicationFileStatus.THEORY_EXAM_SCHEDULED;
		} else if (theoryFailed > 0) {
			newStatus = ApplicationFileStatus.THEORY_FAILED;
		} else {
			newStatus = ApplicationFileStatus.IN_PROGRESS;
		}

		applicationFile.setStatus(newStatus);
		applicationFileService.updateApplicationFile(applicationFile.getId(), applicationFile);
	}
}
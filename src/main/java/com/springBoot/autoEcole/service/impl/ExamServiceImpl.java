package com.springBoot.autoEcole.service.impl;

import com.springBoot.autoEcole.dto.ExamRequestDTO;
import com.springBoot.autoEcole.dto.ExamResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.springBoot.autoEcole.mapper.ExamMapper;
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

	@Autowired
	private ExamMapper examMapper;

	@Override
	public Exam saveExam(Long applicationFileId, ExamRequestDTO examRequest) {
		ApplicationFile applicationFile = applicationFileService.findById(applicationFileId);
		if (applicationFile == null) {
			throw new EntityNotFoundException("Application file not found with ID: " + applicationFileId);
		}

		// Check if application file is still active
		if (!applicationFile.getIsActive()) {
			throw new IllegalStateException("Cannot add exam to inactive application file");
		}

		// Call stored procedure to save exam and handle business logic
		try {
			examDao.saveExamWithBusinessLogic(
					applicationFileId,
					examRequest.getExamType(),
					examRequest.getDate(),
					examRequest.getStatus()
			);

			return examDao.findLatestExamByApplicationFileAndType(applicationFileId, examRequest.getExamType());

		} catch (DataAccessException e) {
			// This will catch both DataIntegrityViolationException and other DataAccessExceptions
			String message = extractErrorMessage(e);
			throw new IllegalStateException(message);
		} catch (Exception e) {
			String message = e.getMessage();
			if (message != null && message.contains("Maximum number of attempts")) {
				throw new IllegalStateException("Maximum number of attempts (3) exceeded for this exam type");
			} else if (message != null && message.contains("Cannot schedule theory exam")) {
				throw new IllegalStateException("Cannot schedule theory exam: there is already a scheduled theory exam. Complete the current exam first.");
			} else if (message != null && message.contains("Cannot schedule practical exam: there is already a scheduled practical exam")) {
				throw new IllegalStateException("Cannot schedule practical exam: there is already a scheduled practical exam. Complete the current exam first.");
			} else if (message != null && message.contains("Cannot schedule practical exam: theory exam must be passed first")) {
				throw new IllegalStateException("Cannot schedule practical exam: theory exam must be passed first.");
			} else {
				throw new RuntimeException("Error saving exam: " + (message != null ? message : "Unknown error"));
			}
		}
	}

	@Override
	public List<ExamResponseDTO> getExamsByApplicationFile(Long applicationFileId) {
		ApplicationFile applicationFile = applicationFileService.findById(applicationFileId);
		if (applicationFile == null) {
			throw new EntityNotFoundException("Application file not found with ID: " + applicationFileId);
		}

		List<Exam> exams = examDao.findByApplicationFileOrderByDateDesc(applicationFile);
		return exams.stream()
				.map(ExamResponseDTO::fromEntity)
				.collect(Collectors.toList());
	}

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
				// Look for our custom error messages
				if (causeMessage.contains("Maximum number of attempts")) {
					return "Maximum number of attempts (3) exceeded for this exam type";
				} else if (causeMessage.contains("Cannot schedule theory exam")) {
					return "Cannot schedule theory exam: there is already a scheduled theory exam. Complete the current exam first.";
				} else if (causeMessage.contains("Cannot schedule practical exam: there is already a scheduled practical exam")) {
					return "Cannot schedule practical exam: there is already a scheduled practical exam. Complete the current exam first.";
				} else if (causeMessage.contains("Cannot schedule practical exam: theory exam must be passed first")) {
					return "Cannot schedule practical exam: theory exam must be passed first.";
				}
			}
			cause = cause.getCause();
		}

		return message;
	}
}
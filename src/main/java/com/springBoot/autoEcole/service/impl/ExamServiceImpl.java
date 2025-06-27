package com.springBoot.autoEcole.service.impl;

import com.springBoot.autoEcole.dto.ExamRequestDTO;
import com.springBoot.autoEcole.dto.ExamResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
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
			// This will call your PL/SQL procedure
			examDao.saveExamWithBusinessLogic(
					applicationFileId,
					examRequest.getExamType(),
					examRequest.getDate(),
					examRequest.getStatus()
			);

			// Return the latest exam for this application file and type
			return examDao.findLatestExamByApplicationFileAndType(applicationFileId, examRequest.getExamType());

		} catch (Exception e) {
			throw new RuntimeException("Error saving exam: " + e.getMessage());
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
}
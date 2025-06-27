package com.springBoot.autoEcole.service;

import com.springBoot.autoEcole.dto.ExamRequestDTO;
import com.springBoot.autoEcole.dto.ExamResponseDTO;
import com.springBoot.autoEcole.model.Exam;

import java.util.List;

public interface ExamService {
	Exam saveExam(Long applicationFileId, ExamRequestDTO examRequest);
	List<ExamResponseDTO> getExamsByApplicationFile(Long applicationFileId);
}
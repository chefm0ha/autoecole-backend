package com.springBoot.autoEcole.service;

import com.springBoot.autoEcole.model.Exam;

public interface ExamService {
	Exam saveExam(Long applicationFileId, Exam exam);
	Long deleteExam(Long id);
}
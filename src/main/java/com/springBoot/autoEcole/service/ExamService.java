package com.springBoot.autoEcole.service;

import com.springBoot.autoEcole.model.Exam;

public interface ExamService {
	Exam saveExam(String candidateCin, Exam exam);
	Long deleteExam(Long id);
}
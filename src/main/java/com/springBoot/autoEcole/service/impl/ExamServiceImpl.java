package com.springBoot.autoEcole.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.springBoot.autoEcole.mapper.ExamMapper;
import com.springBoot.autoEcole.model.Candidate;
import com.springBoot.autoEcole.model.Exam;
import com.springBoot.autoEcole.repository.ExamDao;
import com.springBoot.autoEcole.service.CandidateService;
import com.springBoot.autoEcole.service.ExamService;

@Service
@Transactional
public class ExamServiceImpl implements ExamService {

	@Autowired
	private CandidateService candidateService;

	@Autowired
	private ExamDao examDao;

	@Autowired
	private ExamMapper examMapper;

	@Override
	public Exam saveExam(String candidateCin, Exam exam) {
		Candidate candidate = candidateService.findByCin(candidateCin);

		// Set attempt number based on previous exams
		long previousAttempts = examDao.countByCandidateAndExamType(candidate, exam.getExamType());
		exam.setAttemptNumber((int) (previousAttempts + 1));

		Exam examToSave = examMapper.toEntity(exam, candidate);
		return examDao.save(examToSave);
	}

	@Override
	public Long deleteExam(Long id) {
		return examDao.removeById(id);
	}
}
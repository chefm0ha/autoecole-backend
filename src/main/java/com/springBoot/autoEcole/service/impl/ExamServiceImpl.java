package com.springBoot.autoEcole.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.springBoot.autoEcole.mapper.ExamMapper;
import com.springBoot.autoEcole.model.ApplicationFile;
import com.springBoot.autoEcole.model.Exam;
import com.springBoot.autoEcole.repository.ExamDao;
import com.springBoot.autoEcole.service.ApplicationFileService;
import com.springBoot.autoEcole.service.ExamService;

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
	public Exam saveExam(Long applicationFileId, Exam exam) {
		ApplicationFile applicationFile = applicationFileService.findById(applicationFileId);

		Exam examToSave = examMapper.toEntity(exam, applicationFile);
		return examDao.save(examToSave);
	}

	@Override
	public Long deleteExam(Long id) {
		return examDao.removeById(id);
	}
}
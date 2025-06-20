package com.springBoot.autoEcole.service.impl;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springBoot.autoEcole.model.Candidate;
import com.springBoot.autoEcole.service.CandidateService;
import com.springBoot.autoEcole.service.TrainingExamService;

@Service
@Transactional
public class TrainingExamServiceImpl implements TrainingExamService{

	@Autowired
	private CandidateService candidateService;
	@Autowired
	private ITrainingExamDao trainingExamDao;
	
	
	@Override
	public TrainingExam saveTrainingExam(String candidatId, TrainingExam test) {
		Candidate candidate = candidateService.findByCin(candidatId);
		TrainingExam testC = new TrainingExam();
		testC.setCandidate(candidate);
		testC.setDateTraining(test.getDateTraining());
		testC.setNumSerie(test.getNumSerie());
		testC.setScore(test.getScore());
		trainingExamDao.save(testC);
		return testC;
	}


	@Override
	public Collection<TrainingExam> getLastTrainingsByCandidat(String candidatId) {
		Candidate candidate = candidateService.findByCin(candidatId);
		return trainingExamDao.findTop30BycandidatOrderByIdDesc(candidate);
	}

}

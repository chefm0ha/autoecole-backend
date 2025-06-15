package com.springBoot.autoEcole.service.impl;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springBoot.autoEcole.model.Candidat;
import com.springBoot.autoEcole.model.TrainingExam;
import com.springBoot.autoEcole.repository.ITrainingExamDao;
import com.springBoot.autoEcole.service.CandidatService;
import com.springBoot.autoEcole.service.TrainingExamService;

@Service
@Transactional
public class TrainingExamServiceImpl implements TrainingExamService{

	@Autowired
	private CandidatService candidatService;
	@Autowired
	private ITrainingExamDao trainingExamDao;
	
	
	@Override
	public TrainingExam saveTrainingExam(String candidatId, TrainingExam test) {
		Candidat candidat = candidatService.findById(candidatId);
		TrainingExam testC = new TrainingExam();
		testC.setCandidat(candidat);
		testC.setDateTraining(test.getDateTraining());
		testC.setNumSerie(test.getNumSerie());
		testC.setScore(test.getScore());
		trainingExamDao.save(testC);
		return testC;
	}


	@Override
	public Collection<TrainingExam> getLastTrainingsByCandidat(String candidatId) {
		Candidat candidat = candidatService.findById(candidatId);
		return trainingExamDao.findTop30BycandidatOrderByIdDesc(candidat);
	}

}

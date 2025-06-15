package com.springBoot.autoEcole.service;

import java.util.Collection;

import com.springBoot.autoEcole.model.TrainingExam;

public interface TrainingExamService {

	TrainingExam saveTrainingExam(String id, TrainingExam test);

	Collection<TrainingExam> getLastTrainingsByCandidat(String candidatId);

}

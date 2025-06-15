package com.springBoot.autoEcole.repository;

import java.util.Collection;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.springBoot.autoEcole.model.Candidat;
import com.springBoot.autoEcole.model.TrainingExam;

@Repository
@Transactional
public interface ITrainingExamDao extends CrudRepository<TrainingExam, Long>{
	Collection<TrainingExam> findTop30BycandidatOrderByIdDesc(Candidat candidat);

}

package com.springBoot.autoEcole.repository;

import java.util.Collection;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.springBoot.autoEcole.model.Candidate;

@Repository
@Transactional
public interface IDrivingSessionDao extends CrudRepository<DrivingSession, Long>{

	Long removeById(Long id);
	Collection<DrivingSession> findByCandidatOrderByDateDrivingSessionAsc(Candidate candidate);

}

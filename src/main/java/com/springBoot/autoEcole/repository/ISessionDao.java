package com.springBoot.autoEcole.repository;

import java.util.Collection;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.springBoot.autoEcole.model.Candidate;
import com.springBoot.autoEcole.model.Session;

@Repository
@Transactional
public interface ISessionDao extends CrudRepository<Session, Long> {

	Long removeById(Long id);

	Collection<Session> findByCandidateOrderByDateSessionAsc(Candidate candidate);

	Collection<Session> findByCandidateAndSessionType(Candidate candidate, String sessionType);
}
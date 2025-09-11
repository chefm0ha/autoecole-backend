package com.autoecole.repository;

import java.util.Collection;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.autoecole.model.Candidate;
import com.autoecole.model.Session;

@Repository
@Transactional
public interface SessionDao extends CrudRepository<Session, Long> {

	Long removeById(Long id);

	Collection<Session> findByCandidateOrderByDateSessionAsc(Candidate candidate);

	Collection<Session> findByCandidateAndSessionType(Candidate candidate, String sessionType);
}
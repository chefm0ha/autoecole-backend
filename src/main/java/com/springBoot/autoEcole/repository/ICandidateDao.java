package com.springBoot.autoEcole.repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.springBoot.autoEcole.model.Candidate;

@Repository
@Transactional
public interface ICandidateDao extends CrudRepository<Candidate, String> {

	Collection<Candidate> findByIsActive(Boolean isActive);

	Long removeByCin(String cin);

	Candidate findByCin(String cin);

	Optional<Candidate> findByCin(String cin);

	@Query("SELECT COUNT(c) FROM Candidate c WHERE c.startingDate >= :firstDayInYear")
	Integer findCandidatesThisYear(@Param("firstDayInYear") LocalDate firstDayInYear);

	Candidate findByCinAndIsActive(String cin, Boolean isActive);
}
package com.springBoot.autoEcole.repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.springBoot.autoEcole.model.Candidate;

@Repository
@Transactional
public interface CandidateDao extends CrudRepository<Candidate, String> {

	Long removeByCin(String cin);

	Optional<Candidate> findByCin(String cin);

	@Query("SELECT COUNT(c) FROM Candidate c WHERE c.startingDate >= :firstDayInYear")
	Integer findCandidatesThisYear(@Param("firstDayInYear") LocalDate firstDayInYear);

	Page<Candidate> findAll(Pageable pageable);

	Page<Candidate> findByIsActive(Boolean isActive, Pageable pageable);

	@Query("SELECT c FROM Candidate c WHERE " +
			"(:firstName IS NULL OR LOWER(c.firstName) LIKE LOWER(CONCAT('%', :firstName, '%'))) AND " +
			"(:lastName IS NULL OR LOWER(c.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))) AND " +
			"(:cin IS NULL OR LOWER(c.cin) LIKE LOWER(CONCAT('%', :cin, '%'))) AND " +
			"(:isActive IS NULL OR c.isActive = :isActive)")
	Page<Candidate> searchCandidates(
			@Param("firstName") String firstName,
			@Param("lastName") String lastName,
			@Param("cin") String cin,
			@Param("isActive") Boolean isActive,
			Pageable pageable
	);
}
package com.springBoot.autoEcole.repository;

import java.util.Collection;
import java.util.Date;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.springBoot.autoEcole.model.Candidate;


@Repository
@Transactional
public interface ICandidatDao extends CrudRepository<Candidate, String>  {
	public Collection<Candidate> findByActif(Boolean actif);
	
	public Long removeById(String id);
	
	public Candidate findByCin(String cin);
	
	public Optional<Candidate> findById(String id);
	
	@Query("select count(c) from Candidate c "
			+ " where c.startingDate>= :firstDayInYear")
	public Integer findCandidatsYear(@Param("firstDayInYear")Date firstDayInYear);

	public Candidate findByIdAndActif(String id, Boolean actif);
}

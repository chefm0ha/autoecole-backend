package com.springBoot.autoEcole.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.springBoot.autoEcole.model.TechnicalVisit;

@Repository
@Transactional
public interface ITechnicalVisitDao extends CrudRepository<TechnicalVisit, Long> {
	public Long removeById(Long id);

}

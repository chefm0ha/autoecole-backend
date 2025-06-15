package com.springBoot.autoEcole.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.springBoot.autoEcole.model.OilChange;


@Repository
@Transactional
public interface IOilChangeDao extends CrudRepository<OilChange, Long>{

	public Long removeById(Long id);

}

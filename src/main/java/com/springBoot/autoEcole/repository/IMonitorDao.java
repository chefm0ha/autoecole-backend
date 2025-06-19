package com.springBoot.autoEcole.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.springBoot.autoEcole.model.Instructor;

@Repository
@Transactional
public interface IMonitorDao  extends CrudRepository<Instructor, String>{
	public Instructor findByCin(String cin);

	public Long removeByCin(String cin);
}

package com.springBoot.autoEcole.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.springBoot.autoEcole.model.Monitor;

@Repository
@Transactional
public interface IMonitorDao  extends CrudRepository<Monitor, String>{
	public Monitor findByCin(String cin);

	public Long removeByCin(String cin);
}

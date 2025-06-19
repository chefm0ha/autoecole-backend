package com.springBoot.autoEcole.service.impl;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springBoot.autoEcole.model.Instructor;
import com.springBoot.autoEcole.repository.IMonitorDao;
import com.springBoot.autoEcole.service.MonitorService;
@Service
@Transactional
public class MonitorServiceImpl implements MonitorService{

	@Autowired
	private IMonitorDao monitorDao;
	@Override
	public Collection<Instructor> findAllMonitor() {
		return (Collection<Instructor>) monitorDao.findAll();
	}

	@Override
	public Instructor saveMonitor(Instructor instructor) {
		return monitorDao.save(instructor);
	}

	@Override
	public Instructor findByCin(String cin) {
		return monitorDao.findByCin(cin);

	}

	@Override
	public Long deleteMonitor(String cin) {
		return monitorDao.removeByCin(cin);

	}

}

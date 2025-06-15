package com.springBoot.autoEcole.service.impl;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springBoot.autoEcole.model.Monitor;
import com.springBoot.autoEcole.repository.IMonitorDao;
import com.springBoot.autoEcole.service.MonitorService;
@Service
@Transactional
public class MonitorServiceImpl implements MonitorService{

	@Autowired
	private IMonitorDao monitorDao;
	@Override
	public Collection<Monitor> findAllMonitor() {
		return (Collection<Monitor>) monitorDao.findAll();
	}

	@Override
	public Monitor saveMonitor(Monitor monitor) {
		return monitorDao.save(monitor);
	}

	@Override
	public Monitor findByCin(String cin) {
		return monitorDao.findByCin(cin);

	}

	@Override
	public Long deleteMonitor(String cin) {
		return monitorDao.removeByCin(cin);

	}

}

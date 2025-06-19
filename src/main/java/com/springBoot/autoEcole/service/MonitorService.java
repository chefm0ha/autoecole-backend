package com.springBoot.autoEcole.service;

import java.util.Collection;

import com.springBoot.autoEcole.model.Instructor;

public interface MonitorService {

	Collection<Instructor> findAllMonitor();

	Instructor saveMonitor(Instructor instructor);

	Instructor findByCin(String cin);

	Long deleteMonitor(String cin);

}

package com.springBoot.autoEcole.service;

import java.util.Collection;

import com.springBoot.autoEcole.model.Monitor;

public interface MonitorService {

	Collection<Monitor> findAllMonitor();

	Monitor saveMonitor(Monitor monitor);

	Monitor findByCin(String cin);

	Long deleteMonitor(String cin);

}

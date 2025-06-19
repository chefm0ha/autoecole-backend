package com.springBoot.autoEcole.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springBoot.autoEcole.model.TechnicalVisit;
import com.springBoot.autoEcole.model.Vehicle;
import com.springBoot.autoEcole.repository.TechnicalVisitDao;
import com.springBoot.autoEcole.service.TechnicalVisitService;
import com.springBoot.autoEcole.service.VehicleService;

@Service
@Transactional
public class TechnicalVisitServiceImpl implements TechnicalVisitService {

	@Autowired
	private VehicleService vehicleService;
	
	@Autowired
	private TechnicalVisitDao technicalVisitDao;
	
	@Override
	public Long deleteTechnicalVisit(Long id) {
		return technicalVisitDao.removeById(id);
	}

	@Override
	public TechnicalVisit saveTechnicalVisit(String immatVehicle, TechnicalVisit technicalVisit) {
		Vehicle vehicle = vehicleService.findByImmat(immatVehicle);
		TechnicalVisit technicalVisitC = new TechnicalVisit();
		technicalVisitC.setVehicle(vehicle);
		technicalVisitC.setAmount(technicalVisit.getAmount());
		technicalVisitC.setOperationDate(technicalVisit.getOperationDate());
		technicalVisitC.setNextOperationDate(technicalVisit.getNextOperationDate());
		technicalVisitC.setSociety(technicalVisit.getSociety());
		technicalVisitDao.save(technicalVisitC);
		return technicalVisitC;
	}

}

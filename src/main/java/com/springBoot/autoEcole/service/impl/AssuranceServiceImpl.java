package com.springBoot.autoEcole.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springBoot.autoEcole.model.Assurance;
import com.springBoot.autoEcole.model.Vehicle;
import com.springBoot.autoEcole.repository.IAssuranceDao;
import com.springBoot.autoEcole.service.AssuranceService;
import com.springBoot.autoEcole.service.VehicleService;

@Service
@Transactional
public class AssuranceServiceImpl implements AssuranceService{

	@Autowired
	private IAssuranceDao assuranceDao;
	
	@Autowired
	private VehicleService vehicleService;
	
	@Override
	public Long deleteAssurance(Long id) {
		return assuranceDao.removeById(id);
	}

	@Override
	public Assurance saveAssurance(String immatVehicle, Assurance assurance) {
		Vehicle vehicle = vehicleService.findByImmat(immatVehicle);
		Assurance assuranceC = new Assurance();
		assuranceC.setVehicle(vehicle);
		assuranceC.setAmount(assurance.getAmount());
		assuranceC.setOperationDate(assurance.getOperationDate());
		assuranceC.setNextOperationDate(assurance.getNextOperationDate());
		assuranceC.setSociety(assurance.getSociety());
		assuranceDao.save(assuranceC);
		return assuranceC;
	}

}

package com.springBoot.autoEcole.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springBoot.autoEcole.model.Insurance;
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
	public Insurance saveAssurance(String immatVehicle, Insurance insurance) {
		Vehicle vehicle = vehicleService.findByImmat(immatVehicle);
		Insurance insuranceC = new Insurance();
		insuranceC.setVehicle(vehicle);
		insuranceC.setAmount(insurance.getAmount());
		insuranceC.setOperationDate(insurance.getOperationDate());
		insuranceC.setNextOperationDate(insurance.getNextOperationDate());
		insuranceC.setSociety(insurance.getSociety());
		assuranceDao.save(insuranceC);
		return insuranceC;
	}

}

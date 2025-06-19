package com.springBoot.autoEcole.service.impl;

import com.springBoot.autoEcole.mapper.InsuranceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springBoot.autoEcole.model.Insurance;
import com.springBoot.autoEcole.model.Vehicle;
import com.springBoot.autoEcole.repository.InsuranceDao;
import com.springBoot.autoEcole.service.InsuranceService;
import com.springBoot.autoEcole.service.VehicleService;

import java.util.Collection;

@Service
@Transactional
public class InsuranceServiceImpl implements InsuranceService{

	@Autowired
	private InsuranceDao insuranceDao;
	
	@Autowired
	private VehicleService vehicleService;

	@Autowired
	private InsuranceMapper insuranceMapper;

	@Override
	public Collection<Insurance> findByVehicle(String immatVehicle) {
		Vehicle vehicle = vehicleService.findByImmat(immatVehicle);
		return vehicle.getInsurances();
	}

	@Override
	public Insurance saveInsurance(String immatVehicle, Insurance insurance) {
		Vehicle vehicle = vehicleService.findByImmat(immatVehicle);
		Insurance insuranceToSave = insuranceMapper.toEntity(insurance, vehicle);
		return insuranceDao.save(insuranceToSave);
	}

	@Override
	public Long deleteInsurance(Long id) {
		return insuranceDao.removeById(id);
	}

}

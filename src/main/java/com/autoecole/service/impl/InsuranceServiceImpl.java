package com.autoecole.service.impl;

import java.util.Collection;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.autoecole.mapper.InsuranceMapper;
import com.autoecole.model.Insurance;
import com.autoecole.model.Vehicle;
import com.autoecole.repository.InsuranceDao;
import com.autoecole.service.InsuranceService;
import com.autoecole.service.VehicleService;

@Service
@Transactional
@AllArgsConstructor
public class InsuranceServiceImpl implements InsuranceService {

	private final InsuranceDao insuranceDao;
	private final VehicleService vehicleService;
	private final InsuranceMapper insuranceMapper;

	@Override
	public Collection<Insurance> findByVehicle(String immatVehicle) {
		Vehicle vehicle = vehicleService.findByImmat(immatVehicle);
		return insuranceDao.findByVehicle(vehicle);
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
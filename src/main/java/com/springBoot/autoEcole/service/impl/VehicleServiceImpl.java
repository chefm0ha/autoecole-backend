package com.springBoot.autoEcole.service.impl;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springBoot.autoEcole.model.Vehicle;
import com.springBoot.autoEcole.repository.IVehicleDao;
import com.springBoot.autoEcole.service.VehicleService;

@Service
@Transactional
public class VehicleServiceImpl implements VehicleService{

	@Autowired
	private IVehicleDao vehicleDao;
	
	@Override
	public Collection<Vehicle> findAllVehicle() {
		return (Collection<Vehicle>) vehicleDao.findAll();
	}

	@Override
	public Vehicle saveVehicle(Vehicle vehicle) {
		return vehicleDao.save(vehicle);
	}

	@Override
	public Vehicle findByImmat(String immat) {
		return vehicleDao.findByImmatriculation(immat);
	}

	@Override
	public Long deleteVehicle(String immat) {
		return vehicleDao.removeByImmatriculation(immat);
	}

}

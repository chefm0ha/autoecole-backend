package com.springBoot.autoEcole.service;

import java.util.Collection;

import com.springBoot.autoEcole.model.Vehicle;

public interface VehicleService {
	Collection<Vehicle> findAllVehicle();
	Vehicle saveVehicle(Vehicle vehicle);
	Vehicle findByImmat(String immat);
	Long deleteVehicle(String immat);
}

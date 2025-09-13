package com.autoecole.service;

import java.util.Collection;

import com.autoecole.dto.response.VehicleDTO;
import com.autoecole.model.Vehicle;

public interface VehicleService {
	Collection<Vehicle> findAllVehicle();
	Collection<VehicleDTO> findVehicleByCode(Long appFileId);
	Vehicle saveVehicle(Vehicle vehicle);
	Vehicle findByImmat(String immat);
	Long deleteVehicle(String immat);
	void updateVehiclesQuotaMonthly();
}

package com.autoecole.service;

import java.util.Collection;
import com.autoecole.model.Insurance;

public interface InsuranceService {
	Collection<Insurance> findByVehicle(String immatVehicle);
	Insurance saveInsurance(String immatVehicle, Insurance insurance);
	Long deleteInsurance(Long id);
}
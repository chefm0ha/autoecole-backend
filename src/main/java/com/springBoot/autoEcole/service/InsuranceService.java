package com.springBoot.autoEcole.service;

import java.util.Collection;
import com.springBoot.autoEcole.model.Insurance;

public interface InsuranceService {
	Collection<Insurance> findByVehicle(String immatVehicle);
	Insurance saveInsurance(String immatVehicle, Insurance insurance);
	Long deleteInsurance(Long id);
}
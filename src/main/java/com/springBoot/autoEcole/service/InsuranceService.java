package com.springBoot.autoEcole.service;

import com.springBoot.autoEcole.model.Insurance;

import java.util.Collection;

public interface InsuranceService {

	Collection<Insurance> findByVehicle(String immatVehicle);

	Insurance saveInsurance(String immatVehicle, Insurance insurance);

	Long deleteAssurance(Long id);

}

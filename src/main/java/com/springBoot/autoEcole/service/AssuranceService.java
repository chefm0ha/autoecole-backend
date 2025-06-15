package com.springBoot.autoEcole.service;

import com.springBoot.autoEcole.model.Assurance;

public interface AssuranceService {

	Long deleteAssurance(Long id);

	Assurance saveAssurance(String immatVehicle, Assurance assurance);

}

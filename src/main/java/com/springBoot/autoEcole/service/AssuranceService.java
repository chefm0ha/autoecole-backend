package com.springBoot.autoEcole.service;

import com.springBoot.autoEcole.model.Insurance;

public interface AssuranceService {

	Long deleteAssurance(Long id);

	Insurance saveAssurance(String immatVehicle, Insurance insurance);

}

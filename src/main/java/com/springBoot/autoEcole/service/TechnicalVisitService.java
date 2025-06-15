package com.springBoot.autoEcole.service;

import com.springBoot.autoEcole.model.TechnicalVisit;

public interface TechnicalVisitService {

	Long deleteTechnicalVisit(Long id);

	TechnicalVisit saveTechnicalVisit(String immatVehicle, TechnicalVisit technicalVisit);

}

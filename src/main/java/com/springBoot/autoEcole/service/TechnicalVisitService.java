package com.springBoot.autoEcole.service;

import com.springBoot.autoEcole.model.TechnicalVisit;

public interface TechnicalVisitService {
	TechnicalVisit saveTechnicalVisit(String immatVehicle, TechnicalVisit technicalVisit);
	Long deleteTechnicalVisit(Long id);
}
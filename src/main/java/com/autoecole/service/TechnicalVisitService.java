package com.autoecole.service;

import com.autoecole.model.TechnicalVisit;

public interface TechnicalVisitService {
	TechnicalVisit saveTechnicalVisit(String immatVehicle, TechnicalVisit technicalVisit);
	Long deleteTechnicalVisit(Long id);
}
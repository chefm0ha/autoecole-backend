package com.springBoot.autoEcole.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.springBoot.autoEcole.mapper.TechnicalVisitMapper;
import com.springBoot.autoEcole.model.TechnicalVisit;
import com.springBoot.autoEcole.model.Vehicle;
import com.springBoot.autoEcole.repository.TechnicalVisitDao;
import com.springBoot.autoEcole.service.TechnicalVisitService;
import com.springBoot.autoEcole.service.VehicleService;

@Service
@Transactional
public class TechnicalVisitServiceImpl implements TechnicalVisitService {

	@Autowired
	private VehicleService vehicleService;

	@Autowired
	private TechnicalVisitDao technicalVisitDao;

	@Autowired
	private TechnicalVisitMapper technicalVisitMapper;

	@Override
	public TechnicalVisit saveTechnicalVisit(String immatVehicle, TechnicalVisit technicalVisit) {
		Vehicle vehicle = vehicleService.findByImmat(immatVehicle);
		TechnicalVisit technicalVisitToSave = technicalVisitMapper.toEntity(technicalVisit, vehicle);
		return technicalVisitDao.save(technicalVisitToSave);
	}

	@Override
	public Long deleteTechnicalVisit(Long id) {
		return technicalVisitDao.removeById(id);
	}
}
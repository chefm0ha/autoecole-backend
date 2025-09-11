package com.autoecole.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.autoecole.mapper.TechnicalVisitMapper;
import com.autoecole.model.TechnicalVisit;
import com.autoecole.model.Vehicle;
import com.autoecole.repository.TechnicalVisitDao;
import com.autoecole.service.TechnicalVisitService;
import com.autoecole.service.VehicleService;

@Service
@Transactional
@AllArgsConstructor
public class TechnicalVisitServiceImpl implements TechnicalVisitService {

	private final VehicleService vehicleService;
	private final TechnicalVisitDao technicalVisitDao;
	private final TechnicalVisitMapper technicalVisitMapper;

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
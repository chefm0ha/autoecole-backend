package com.springBoot.autoEcole.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.springBoot.autoEcole.mapper.OilChangeMapper;
import com.springBoot.autoEcole.model.OilChange;
import com.springBoot.autoEcole.model.Vehicle;
import com.springBoot.autoEcole.repository.OilChangeDao;
import com.springBoot.autoEcole.service.OilChangeService;
import com.springBoot.autoEcole.service.VehicleService;

@Service
@Transactional
public class OilChangeServiceImpl implements OilChangeService {

	@Autowired
	private VehicleService vehicleService;

	@Autowired
	private OilChangeDao oilChangeDao;

	@Autowired
	private OilChangeMapper oilChangeMapper;

	@Override
	public OilChange saveOilChange(String immatVehicle, OilChange oilChange) {
		Vehicle vehicle = vehicleService.findByImmat(immatVehicle);
		OilChange oilChangeToSave = oilChangeMapper.toEntity(oilChange, vehicle);
		return oilChangeDao.save(oilChangeToSave);
	}

	@Override
	public Long deleteOilChange(Long id) {
		return oilChangeDao.removeById(id);
	}
}
package com.autoecole.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.autoecole.mapper.OilChangeMapper;
import com.autoecole.model.OilChange;
import com.autoecole.model.Vehicle;
import com.autoecole.repository.OilChangeDao;
import com.autoecole.service.OilChangeService;
import com.autoecole.service.VehicleService;

@Service
@Transactional
@AllArgsConstructor
public class OilChangeServiceImpl implements OilChangeService {

	private final VehicleService vehicleService;
	private final OilChangeDao oilChangeDao;
	private final OilChangeMapper oilChangeMapper;

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
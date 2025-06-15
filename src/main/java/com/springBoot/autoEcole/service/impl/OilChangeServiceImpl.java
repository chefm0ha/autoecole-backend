package com.springBoot.autoEcole.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springBoot.autoEcole.model.OilChange;
import com.springBoot.autoEcole.model.Vehicle;
import com.springBoot.autoEcole.repository.IOilChangeDao;
import com.springBoot.autoEcole.service.OilChangeService;
import com.springBoot.autoEcole.service.VehicleService;

@Service
@Transactional
public class OilChangeServiceImpl implements OilChangeService{
	@Autowired
	private VehicleService vehicleService;
	@Autowired
	private IOilChangeDao oilChangeDao;
	
	@Override
	public OilChange saveOilChange(String immatVehicle, OilChange oilChange) {
		Vehicle vehicle = vehicleService.findByImmat(immatVehicle);
		OilChange oilChangeC = new OilChange();
		oilChangeC.setVehicle(vehicle);
		oilChangeC.setActualKm(oilChange.getActualKm());
		oilChangeC.setAmount(oilChange.getAmount());
		oilChangeC.setOperationDate(oilChange.getOperationDate());
		oilChangeC.setNextOperationDate(oilChange.getNextOperationDate());
		oilChangeC.setSociety(oilChange.getSociety());
		oilChangeDao.save(oilChangeC);
		return oilChangeC;
	}

	@Override
	public Long deleteOilChange(Long id) {
		return oilChangeDao.removeById(id);
	}

}

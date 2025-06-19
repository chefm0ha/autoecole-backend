package com.springBoot.autoEcole.service;

import com.springBoot.autoEcole.model.OilChange;

public interface OilChangeService {
	OilChange saveOilChange(String immatVehicle, OilChange oilChange);
	public Long deleteOilChange(Long id);
}

package com.autoecole.service;

import com.autoecole.model.OilChange;

public interface OilChangeService {
	OilChange saveOilChange(String immatVehicle, OilChange oilChange);
	public Long deleteOilChange(Long id);
}

package com.springBoot.autoEcole.web.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.springBoot.autoEcole.model.OilChange;
import com.springBoot.autoEcole.service.OilChangeService;

@RestController
@RequestMapping("/oilChange")
@CrossOrigin
public class OilChangeFacade {

	@Autowired
	private OilChangeService oilChangeService;

	@PostMapping("/saveOilChange/{immatVehicle}")
	public OilChange saveOilChange(@PathVariable String immatVehicle, @RequestBody OilChange oilChange) {
		return oilChangeService.saveOilChange(immatVehicle, oilChange);
	}

	@GetMapping("/deleteOilChange/{id}")
	public Long deleteOilChange(@PathVariable Long id) {
		return oilChangeService.deleteOilChange(id);
	}
}
package com.autoecole.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.autoecole.model.OilChange;
import com.autoecole.service.OilChangeService;

@RestController
@RequestMapping("/oilChange")
@CrossOrigin
@AllArgsConstructor
public class OilChangeFacade {

	private final OilChangeService oilChangeService;

	@PostMapping("/saveOilChange/{immatVehicle}")
	public OilChange saveOilChange(
			@PathVariable String immatVehicle,
			@RequestBody OilChange oilChange) {
		return oilChangeService.saveOilChange(immatVehicle, oilChange);
	}

	@GetMapping("/deleteOilChange/{id}")
	public Long deleteOilChange(@PathVariable Long id) {
		return oilChangeService.deleteOilChange(id);
	}
}
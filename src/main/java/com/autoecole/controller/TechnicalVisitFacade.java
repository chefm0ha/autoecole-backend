package com.autoecole.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.autoecole.model.TechnicalVisit;
import com.autoecole.service.TechnicalVisitService;

@RestController
@RequestMapping("/technicalVisit")
@CrossOrigin
@AllArgsConstructor
public class TechnicalVisitFacade {

	private final TechnicalVisitService technicalVisitService;

	@PostMapping("/saveTechnicalVisit/{immatVehicle}")
	public TechnicalVisit saveTechnicalVisit(
			@PathVariable String immatVehicle,
			@RequestBody TechnicalVisit technicalVisit) {
		return technicalVisitService.saveTechnicalVisit(immatVehicle, technicalVisit);
	}

	@GetMapping("/deleteTechnicalVisit/{id}")
	public Long deleteTechnicalVisit(@PathVariable Long id) {
		return technicalVisitService.deleteTechnicalVisit(id);
	}
}
package com.springBoot.autoEcole.web.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.springBoot.autoEcole.model.TechnicalVisit;
import com.springBoot.autoEcole.service.TechnicalVisitService;

@RestController
@RequestMapping("/technicalVisit")
@CrossOrigin
public class TechnicalVisitFacade {

	@Autowired
	private TechnicalVisitService technicalVisitService;

	@PostMapping("/saveTechnicalVisit/{immatVehicle}")
	public TechnicalVisit saveTechnicalVisit(@PathVariable String immatVehicle, @RequestBody TechnicalVisit technicalVisit) {
		return technicalVisitService.saveTechnicalVisit(immatVehicle, technicalVisit);
	}

	@GetMapping("/deleteTechnicalVisit/{id}")
	public Long deleteTechnicalVisit(@PathVariable Long id) {
		return technicalVisitService.deleteTechnicalVisit(id);
	}
}
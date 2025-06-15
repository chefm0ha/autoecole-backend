package com.springBoot.autoEcole.web.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springBoot.autoEcole.model.TechnicalVisit;
import com.springBoot.autoEcole.service.TechnicalVisitService;
import com.sun.istack.NotNull;

@RestController
@RequestMapping("/technicalVisit")
@CrossOrigin
public class TechnicalVisitFacade {

	@Autowired
	private TechnicalVisitService technicalVisitService;
	
	@PostMapping("/saveTechnicalVisit/{immatVehicle}") 
	public TechnicalVisit saveTechnicalVisit(@PathVariable String immatVehicle, @RequestBody TechnicalVisit technicalVisit) {
		return technicalVisitService.saveTechnicalVisit(immatVehicle,technicalVisit);			
	}
	
	@GetMapping("/deleteTechnicalVisit/{id}") 
	public Long deleteTechnicalVisit(@PathVariable @NotNull Long id) {
		return  technicalVisitService.deleteTechnicalVisit(id);			
	}
}

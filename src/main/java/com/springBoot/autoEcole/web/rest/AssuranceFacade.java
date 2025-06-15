package com.springBoot.autoEcole.web.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springBoot.autoEcole.model.Assurance;
import com.springBoot.autoEcole.service.AssuranceService;
import com.sun.istack.NotNull;

@RestController
@RequestMapping("/assurance")
@CrossOrigin
public class AssuranceFacade {

	@Autowired
	private AssuranceService assuranceService;
	
	
	@PostMapping("/saveAssurance/{immatVehicle}") 
	public Assurance saveAssurance(@PathVariable String immatVehicle, @RequestBody Assurance assurance) {
		return assuranceService.saveAssurance(immatVehicle,assurance);			
	}
	@GetMapping("/deleteAssurance/{id}") 
	public Long deleteAssurance(@PathVariable @NotNull Long id) {
		return  assuranceService.deleteAssurance(id);			
	}
}

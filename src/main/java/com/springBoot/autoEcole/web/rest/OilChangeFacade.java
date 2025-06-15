package com.springBoot.autoEcole.web.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springBoot.autoEcole.model.OilChange;
import com.springBoot.autoEcole.service.OilChangeService;
import com.sun.istack.NotNull;

@RestController
@RequestMapping("/oilChange")
@CrossOrigin
public class OilChangeFacade {

	@Autowired
	private OilChangeService oilChangeService;
	
	@PostMapping("/saveOilChange/{immatVehicle}") 
	public OilChange saveOilChange(@PathVariable String immatVehicle, @RequestBody OilChange oilChange) {
		return oilChangeService.saveOilChange(immatVehicle,oilChange);			
	}
	
	@GetMapping("/deleteOilChange/{id}") 
	public Long deleteOilChange(@PathVariable @NotNull Long id) {
		return  oilChangeService.deleteOilChange(id);			
	}
}

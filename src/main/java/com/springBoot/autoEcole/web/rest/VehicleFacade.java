package com.springBoot.autoEcole.web.rest;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springBoot.autoEcole.model.Vehicle;
import com.springBoot.autoEcole.service.VehicleService;
import com.sun.istack.NotNull;

@RestController
@RequestMapping("/vehicle")
@CrossOrigin
public class VehicleFacade {

	@Autowired
	private VehicleService vehicleService;
	
	@GetMapping("/getVehicles") 
	public Collection<Vehicle> getVehicles() {
		return  vehicleService.findAllVehicle();	
	}
	
	@PostMapping("/saveVehicle") 
	public Vehicle saveVehicle(@RequestBody Vehicle vehicle) {
		return  vehicleService.saveVehicle(vehicle);			
	}
	@GetMapping("/getVehicle/{immat}") 
	public Vehicle getVehicleByImmat(@PathVariable @NotNull String immat) {
		return  vehicleService.findByImmat(immat);
	}
	
	@GetMapping("/deleteVehicle/{immat}") 
	public Long deleteVehicle(@PathVariable @NotNull String immat) {
		return  vehicleService.deleteVehicle(immat);			
	}
}

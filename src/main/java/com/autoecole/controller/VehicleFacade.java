package com.autoecole.controller;

import java.util.Collection;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autoecole.model.Vehicle;
import com.autoecole.service.VehicleService;

@RestController
@RequestMapping("/vehicle")
@CrossOrigin
@AllArgsConstructor
public class VehicleFacade {

	private final VehicleService vehicleService;

	@GetMapping("/getVehicles")
	public Collection<Vehicle> getVehicles() {
		return vehicleService.findAllVehicle();
	}

	@PostMapping("/saveVehicle")
	public Vehicle saveVehicle(@RequestBody Vehicle vehicle) {
		return vehicleService.saveVehicle(vehicle);
	}

	@GetMapping("/getVehicle/{immat}")
	public Vehicle getVehicleByImmat(@PathVariable String immat) {
		return vehicleService.findByImmat(immat);
	}

	@GetMapping("/deleteVehicle/{immat}")
	public Long deleteVehicle(@PathVariable String immat) {
		return vehicleService.deleteVehicle(immat);
	}
}
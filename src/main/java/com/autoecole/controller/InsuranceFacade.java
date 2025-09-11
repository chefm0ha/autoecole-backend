package com.autoecole.controller;

import java.util.Collection;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.autoecole.model.Insurance;
import com.autoecole.service.InsuranceService;

@RestController
@RequestMapping("/insurance")
@CrossOrigin
@AllArgsConstructor
public class InsuranceFacade {

	private final InsuranceService insuranceService;

	@PostMapping("/saveInsurance/{immatVehicle}")
	public Insurance saveInsurance(
			@PathVariable String immatVehicle,
			@RequestBody Insurance insurance) {
		return insuranceService.saveInsurance(immatVehicle, insurance);
	}

	@GetMapping("/getInsuranceByVehicle/{immatVehicle}")
	public Collection<Insurance> getInsuranceByVehicle(@PathVariable String immatVehicle) {
		return insuranceService.findByVehicle(immatVehicle);
	}

	@GetMapping("/deleteInsurance/{id}")
	public Long deleteInsurance(@PathVariable Long id) {
		return insuranceService.deleteInsurance(id);
	}
}
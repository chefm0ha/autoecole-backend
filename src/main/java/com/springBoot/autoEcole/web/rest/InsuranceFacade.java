package com.springBoot.autoEcole.web.rest;

import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.springBoot.autoEcole.model.Insurance;
import com.springBoot.autoEcole.service.InsuranceService;

@RestController
@RequestMapping("/insurance")
@CrossOrigin
public class InsuranceFacade {

	@Autowired
	private InsuranceService insuranceService;

	@PostMapping("/saveInsurance/{immatVehicle}")
	public Insurance saveInsurance(@PathVariable String immatVehicle, @RequestBody Insurance insurance) {
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
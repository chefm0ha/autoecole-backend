package com.autoecole.controller;

import com.autoecole.dto.response.PaymentWithInstallmentsDTO;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.autoecole.service.PaymentService;

@RestController
@RequestMapping("/payment")
@CrossOrigin
@AllArgsConstructor
public class PaymentFacade {

	private final PaymentService paymentService;

	@GetMapping("/getPaymentByApplicationFile/{applicationFileId}")
	public PaymentWithInstallmentsDTO getPaymentByApplicationFile(@PathVariable Long applicationFileId) {
		return paymentService.getPaymentByApplicationFile(applicationFileId);
	}
}
package com.autoecole.controller;

import java.util.Collection;

import com.autoecole.dto.PaymentWithInstallmentsDTO;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.autoecole.model.Payment;
import com.autoecole.service.PaymentService;

@RestController
@RequestMapping("/payment")
@CrossOrigin
@AllArgsConstructor
public class PaymentFacade {

	private final PaymentService paymentService;

	@GetMapping("/getAllPayments")
	public Collection<Payment> getAllPayments() {
		return paymentService.findAllPayment();
	}

	@GetMapping("/getPayment/{id}")
	public Payment getPaymentById(@PathVariable Long id) {
		return paymentService.findById(id);
	}

	@GetMapping("/getPaymentByApplicationFile/{applicationFileId}")
	public PaymentWithInstallmentsDTO getPaymentByApplicationFile(@PathVariable Long applicationFileId) {
		return paymentService.getPaymentByApplicationFile(applicationFileId);
	}
}
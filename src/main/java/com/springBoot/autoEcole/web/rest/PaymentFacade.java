package com.springBoot.autoEcole.web.rest;

import java.util.Collection;

import com.springBoot.autoEcole.dto.PaymentWithInstallmentsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.springBoot.autoEcole.model.Payment;
import com.springBoot.autoEcole.service.PaymentService;

@RestController
@RequestMapping("/payment")
@CrossOrigin
public class PaymentFacade {

	@Autowired
	private PaymentService paymentService;

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
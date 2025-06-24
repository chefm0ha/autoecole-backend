package com.springBoot.autoEcole.web.rest;

import java.util.Collection;
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

	@PostMapping("/savePayment/{applicationFileId}")
	public Payment savePayment(@PathVariable Long applicationFileId, @RequestBody Payment payment) {
		return paymentService.savePayment(applicationFileId, payment);
	}

	@GetMapping("/getPayment/{id}")
	public Payment getPaymentById(@PathVariable Long id) {
		return paymentService.findById(id);
	}

	@GetMapping("/deletePayment/{id}")
	public Long deletePayment(@PathVariable Long id) {
		return paymentService.deletePayment(id);
	}
}
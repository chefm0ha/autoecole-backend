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

import com.springBoot.autoEcole.model.Payment;
import com.springBoot.autoEcole.service.PaymentService;
import com.sun.istack.NotNull;

@RestController
@RequestMapping("/payment")
@CrossOrigin
public class PaymentFacade {
	
	@Autowired
	private PaymentService paymentService;
	
	@GetMapping("/getAllPayment") 
	public Collection<Payment> getAllPayment() {
		return  paymentService.findAllPayment();			
	}
	
	@PostMapping("/savePayment/{candidatId}") 
	public Payment savePayment(@PathVariable String candidatId, @RequestBody Payment payment) {
		return paymentService.savePayment(candidatId,payment);			
	}
	
	@GetMapping("/deletePayment/{id}") 
	public Long deletePayment(@PathVariable @NotNull Long id) {
		return  paymentService.deletePayment(id);			
	}
}

package com.springBoot.autoEcole.service;

import java.util.Collection;

import com.springBoot.autoEcole.model.Payment;

public interface PaymentService {
	
	public Collection<Payment> findAllPayment();

	public Payment savePayment(String id, Payment payment);

	public Long deletePayment(Long id);

}

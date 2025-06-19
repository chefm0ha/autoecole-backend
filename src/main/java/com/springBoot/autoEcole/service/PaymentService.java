package com.springBoot.autoEcole.service;

import java.util.Collection;
import com.springBoot.autoEcole.model.Payment;

public interface PaymentService {
	Collection<Payment> findAllPayment();
	Payment savePayment(String candidateCin, Payment payment);
	Payment findById(Long id);
	Long deletePayment(Long id);
}
package com.springBoot.autoEcole.service;

import java.util.Collection;

import com.springBoot.autoEcole.dto.PaymentWithInstallmentsDTO;
import com.springBoot.autoEcole.model.Payment;

public interface PaymentService {
	Collection<Payment> findAllPayment();
	Payment findById(Long id);
	PaymentWithInstallmentsDTO getPaymentByApplicationFile(Long applicationFileId);
}
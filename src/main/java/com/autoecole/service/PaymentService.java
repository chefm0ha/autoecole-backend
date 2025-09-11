package com.autoecole.service;

import java.util.Collection;

import com.autoecole.dto.PaymentWithInstallmentsDTO;
import com.autoecole.model.Payment;

public interface PaymentService {
	Collection<Payment> findAllPayment();
	Payment findById(Long id);
	PaymentWithInstallmentsDTO getPaymentByApplicationFile(Long applicationFileId);
	PaymentWithInstallmentsDTO getPaymentWithInstallments(Long paymentId);
}
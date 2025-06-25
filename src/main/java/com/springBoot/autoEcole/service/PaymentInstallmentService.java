package com.springBoot.autoEcole.service;

import com.springBoot.autoEcole.model.PaymentInstallment;

public interface PaymentInstallmentService {
    PaymentInstallment findById(Long id);
    PaymentInstallment savePaymentInstallment(Long paymentId, PaymentInstallment paymentInstallment);
}
package com.springBoot.autoEcole.service;

import com.springBoot.autoEcole.dto.PaymentWithInstallmentsDTO;
import com.springBoot.autoEcole.model.PaymentInstallment;

public interface PaymentInstallmentService {
    PaymentInstallment findById(Long id);
    PaymentWithInstallmentsDTO savePaymentInstallment(Long paymentId, Integer amount);
}
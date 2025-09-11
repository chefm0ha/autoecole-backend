package com.autoecole.service;

import com.autoecole.dto.PaymentWithInstallmentsDTO;
import com.autoecole.model.PaymentInstallment;

public interface PaymentInstallmentService {
    PaymentInstallment findById(Long id);
    PaymentWithInstallmentsDTO savePaymentInstallment(Long paymentId, Integer amount);
}
package com.autoecole.service;

import com.autoecole.dto.response.PaymentWithInstallmentsDTO;
import com.autoecole.model.PaymentInstallment;

public interface PaymentInstallmentService {
    PaymentInstallment findById(Long id);
    PaymentWithInstallmentsDTO savePaymentInstallment(Long paymentId, Integer amount);
    PaymentWithInstallmentsDTO validatePaymentInstallment(Long installmentId);
}
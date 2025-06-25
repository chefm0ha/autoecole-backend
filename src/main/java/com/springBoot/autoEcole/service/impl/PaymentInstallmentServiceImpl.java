package com.springBoot.autoEcole.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.springBoot.autoEcole.model.Payment;
import com.springBoot.autoEcole.model.PaymentInstallment;
import com.springBoot.autoEcole.repository.PaymentInstallmentDao;
import com.springBoot.autoEcole.service.PaymentInstallmentService;
import com.springBoot.autoEcole.service.PaymentService;

import javax.persistence.EntityNotFoundException;

@Service
@Transactional
public class PaymentInstallmentServiceImpl implements PaymentInstallmentService {

    @Autowired
    private PaymentInstallmentDao paymentInstallmentDao;

    @Autowired
    private PaymentService paymentService;

    @Override
    public PaymentInstallment findById(Long id) {
        return paymentInstallmentDao.findById(id).orElse(null);
    }

    @Override
    public PaymentInstallment savePaymentInstallment(Long paymentId, PaymentInstallment paymentInstallment) {
        Payment payment = paymentService.findById(paymentId);
        if (payment == null) {
            throw new EntityNotFoundException("Payment not found with ID: " + paymentId);
        }

        // Create payment installment with minimal data
        // Status and other calculations will be handled by database triggers
        PaymentInstallment installmentToSave = new PaymentInstallment();
        installmentToSave.setAmount(paymentInstallment.getAmount());
        installmentToSave.setDate(paymentInstallment.getDate());
        installmentToSave.setInstallmentNumber(paymentInstallment.getInstallmentNumber());
        installmentToSave.setPayment(payment);

        // Status will be set by database triggers (e.g., "PENDING" by default)

        return paymentInstallmentDao.save(installmentToSave);
    }
}
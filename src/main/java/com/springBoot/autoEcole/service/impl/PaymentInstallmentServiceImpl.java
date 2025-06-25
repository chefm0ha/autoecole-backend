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
import java.time.LocalDate;

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

        PaymentInstallment installmentToSave = new PaymentInstallment();
        installmentToSave.setAmount(paymentInstallment.getAmount());
        installmentToSave.setDate(LocalDate.now());
        installmentToSave.setPayment(payment);

        return paymentInstallmentDao.save(installmentToSave);
    }
}
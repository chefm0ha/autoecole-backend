package com.springBoot.autoEcole.service.impl;

import com.springBoot.autoEcole.dto.PaymentWithInstallmentsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.springBoot.autoEcole.model.Payment;
import com.springBoot.autoEcole.model.PaymentInstallment;
import com.springBoot.autoEcole.repository.PaymentInstallmentDao;
import com.springBoot.autoEcole.service.PaymentInstallmentService;
import com.springBoot.autoEcole.service.PaymentService;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;

@Service
@Transactional
public class PaymentInstallmentServiceImpl implements PaymentInstallmentService {

    @Autowired
    private PaymentInstallmentDao paymentInstallmentDao;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private EntityManager entityManager;

    @Override
    public PaymentInstallment findById(Long id) {
        return paymentInstallmentDao.findById(id).orElse(null);
    }

    @Override
    public PaymentWithInstallmentsDTO savePaymentInstallment(Long paymentId, Integer amount) {
        // Verify payment exists
        Payment payment = paymentService.findById(paymentId);
        if (payment == null) {
            throw new EntityNotFoundException("Payment not found with ID: " + paymentId);
        }

        try {
            // Call stored procedure to save installment and update payment
            paymentInstallmentDao.savePaymentInstallmentWithProcedure(paymentId, amount);

            // Clear Hibernate cache to get fresh data
            entityManager.clear();

            // Return updated payment with installments
            return paymentService.getPaymentWithInstallments(paymentId);

        } catch (DataAccessException e) {
            throw new RuntimeException("Error saving payment installment: " + e.getMessage());
        }
    }
}
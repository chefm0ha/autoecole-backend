package com.springBoot.autoEcole.service.impl;

import com.springBoot.autoEcole.dto.PaymentInstallmentDTO;
import com.springBoot.autoEcole.dto.PaymentWithInstallmentsDTO;
import com.springBoot.autoEcole.enums.PaymentStatus;
import com.springBoot.autoEcole.mapper.PaymentInstallmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.springBoot.autoEcole.model.Payment;
import com.springBoot.autoEcole.model.PaymentInstallment;
import com.springBoot.autoEcole.repository.PaymentDao;
import com.springBoot.autoEcole.repository.PaymentInstallmentDao;
import com.springBoot.autoEcole.service.PaymentInstallmentService;
import com.springBoot.autoEcole.service.PaymentService;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;

@Service
@Transactional
public class PaymentInstallmentServiceImpl implements PaymentInstallmentService {

    @Autowired
    private PaymentInstallmentDao paymentInstallmentDao;

    @Autowired
    private PaymentDao paymentDao;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentInstallmentMapper paymentInstallmentMapper;

    @Override
    public PaymentInstallment findById(Long id) {
        return paymentInstallmentDao.findById(id).orElse(null);
    }

    @Override
    public PaymentWithInstallmentsDTO savePaymentInstallment(Long paymentId, Integer amount) {
        // 1. Verify payment exists
        Payment payment = paymentService.findById(paymentId);
        if (payment == null) {
            throw new EntityNotFoundException("Payment not found with ID: " + paymentId);
        }

        // 2. Calculate next installment number
        Integer nextInstallmentNumber = payment.getPaymentInstallments().stream()
            .mapToInt(PaymentInstallment::getInstallmentNumber)
            .max()
            .orElse(0) + 1;

        // 3. Create new payment installment using mapper
        PaymentInstallment newInstallment = paymentInstallmentMapper.toEntity(amount, payment, nextInstallmentNumber);

        paymentInstallmentDao.save(newInstallment);

        // 4. Update payment status and paid amount
        updatePaymentAfterInstallment(amount, payment);

        // 5. Return updated payment with installments
        return paymentService.getPaymentWithInstallments(paymentId)
                .addPaymentInstallmentDTO(PaymentInstallmentDTO.fromEntity(newInstallment));
    }

    private void updatePaymentAfterInstallment(Integer amount, Payment payment) {
        // Calculate total paid amount from all installments
        Integer totalPaidAmount = payment.getPaidAmount() + amount;

        // Update payment status based on total paid vs total amount
        PaymentStatus newStatus;
        if (totalPaidAmount >= payment.getTotalAmount()) {
            newStatus = PaymentStatus.COMPLETED;
        } else if (totalPaidAmount > 0) {
            newStatus = PaymentStatus.PENDING;
        } else {
            newStatus = PaymentStatus.PENDING; // Default to PENDING if no payments made yet
        }

        // Update payment
        payment.setPaidAmount(totalPaidAmount);
        payment.setStatus(newStatus);
        paymentDao.save(payment);
    }
}
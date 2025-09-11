package com.autoecole.service.impl;

import com.autoecole.dto.response.PaymentInstallmentDTO;
import com.autoecole.dto.response.PaymentWithInstallmentsDTO;
import com.autoecole.enums.PaymentStatus;
import com.autoecole.mapper.PaymentInstallmentMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.autoecole.model.Payment;
import com.autoecole.model.PaymentInstallment;
import com.autoecole.repository.PaymentDao;
import com.autoecole.repository.PaymentInstallmentDao;
import com.autoecole.service.PaymentInstallmentService;
import com.autoecole.service.PaymentService;

import javax.persistence.EntityNotFoundException;

@Service
@Transactional
@AllArgsConstructor
public class PaymentInstallmentServiceImpl implements PaymentInstallmentService {

    private final PaymentInstallmentDao paymentInstallmentDao;
    private final PaymentDao paymentDao;
    private final PaymentService paymentService;
    private final PaymentInstallmentMapper paymentInstallmentMapper;

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
        // Calculate the total paid amount from all installments
        Integer totalPaidAmount = payment.getPaidAmount() + amount;

        // Update payment status based on total paid vs. total amount
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
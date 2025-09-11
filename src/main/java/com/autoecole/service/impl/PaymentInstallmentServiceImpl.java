package com.autoecole.service.impl;

import com.autoecole.dto.response.PaymentInstallmentDTO;
import com.autoecole.dto.response.PaymentWithInstallmentsDTO;
import com.autoecole.enums.PaymentInstallmentStatus;
import com.autoecole.enums.PaymentStatus;
import com.autoecole.exception.BusinessException;
import com.autoecole.exception.NotFoundException;
import com.autoecole.mapper.PaymentInstallmentMapper;
import com.autoecole.service.AuthenticationHelper;
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
    private final AuthenticationHelper authenticationHelper;

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

        // 3. Determine installment status based on a user role
        PaymentInstallmentStatus installmentStatus = authenticationHelper.isCurrentUserAdmin()
                ? PaymentInstallmentStatus.VALIDATED
                : PaymentInstallmentStatus.PENDING;

        // 4. Create a new payment installment using mapper with role-based status
        PaymentInstallment newInstallment = paymentInstallmentMapper.toEntity(
                amount, payment, nextInstallmentNumber, installmentStatus);

        paymentInstallmentDao.save(newInstallment);

        // 5. Update payment status and paid amount (only if installment is validated)
        if (installmentStatus == PaymentInstallmentStatus.VALIDATED) {
            updatePaymentAfterInstallment(payment);
        }

        // 6. Return updated payment with installments
        return paymentService.getPaymentWithInstallments(paymentId)
                .addPaymentInstallmentDTO(PaymentInstallmentDTO.fromEntity(newInstallment));
    }

    @Override
    public PaymentWithInstallmentsDTO validatePaymentInstallment(Long installmentId) {
        // 1. Find the installment
        PaymentInstallment installment = paymentInstallmentDao.findById(installmentId)
                .orElseThrow(() -> new NotFoundException("Payment installment not found with ID: " + installmentId));

        // 2. Check if user is admin
        if (!authenticationHelper.isCurrentUserAdmin()) {
            throw new BusinessException("Only administrators can validate payment installments");
        }

        // 3. Check if installment is already validated
        if (installment.getStatus() == PaymentInstallmentStatus.VALIDATED) {
            throw new BusinessException("Payment installment is already validated");
        }

        // 4. Validate the installment
        installment.setStatus(PaymentInstallmentStatus.VALIDATED);
        paymentInstallmentDao.save(installment);

        // 5. Update payment totals
        Payment payment = installment.getPayment();
        updatePaymentAfterInstallment(payment);

        // 6. Return updated payment with installments
        return paymentService.getPaymentWithInstallments(payment.getId());
    }

    private void updatePaymentAfterInstallment(Payment payment) {
        // Calculate the total paid amount from all VALIDATED installments
        int totalValidatedAmount = payment.getPaymentInstallments().stream()
                .filter(installment -> installment.getStatus() == PaymentInstallmentStatus.VALIDATED)
                .mapToInt(PaymentInstallment::getAmount)
                .sum();

        // Update payment status based on total validated vs. total amount
        PaymentStatus newStatus;
        if (totalValidatedAmount >= payment.getTotalAmount()) {
            newStatus = PaymentStatus.COMPLETED;
        } else if (totalValidatedAmount > 0) {
            newStatus = PaymentStatus.PENDING;
        } else {
            newStatus = PaymentStatus.PENDING; // Default to PENDING if no validated payments yet
        }

        // Update payment
        payment.setPaidAmount(totalValidatedAmount);
        payment.setStatus(newStatus);
        paymentDao.save(payment);
    }
}
package com.autoecole.mapper;

import com.autoecole.enums.PaymentInstallmentStatus;
import org.springframework.stereotype.Component;
import com.autoecole.model.PaymentInstallment;
import com.autoecole.model.Payment;

import java.time.LocalDate;

@Component
public class PaymentInstallmentMapper {

    public PaymentInstallment toEntity(Integer amount, Payment payment, Integer installmentNumber, PaymentInstallmentStatus status) {
        if (amount == null || payment == null || installmentNumber == null || status == null) {
            return null;
        }

        PaymentInstallment target = new PaymentInstallment();
        target.setAmount(amount);
        target.setPayment(payment);
        target.setInstallmentNumber(installmentNumber);
        target.setDate(LocalDate.now());
        target.setStatus(status);

        return target;
    }

    public PaymentInstallment toEntity(PaymentInstallment source, Payment payment, Integer installmentNumber, PaymentInstallmentStatus status) {
        if (source == null || payment == null || installmentNumber == null || status == null) {
            return null;
        }

        PaymentInstallment target = new PaymentInstallment();
        target.setAmount(source.getAmount());
        target.setPayment(payment);
        target.setInstallmentNumber(installmentNumber);
        target.setDate(source.getDate() != null ? source.getDate() : LocalDate.now());
        target.setStatus(status);

        return target;
    }

    public void updateEntity(PaymentInstallment target, PaymentInstallment source) {
        if (source == null || target == null) {
            return;
        }

        if (source.getAmount() != null) {
            target.setAmount(source.getAmount());
        }
        if (source.getDate() != null) {
            target.setDate(source.getDate());
        }
        if (source.getStatus() != null) {
            target.setStatus(source.getStatus());
        }
        // Note: payment and installmentNumber are typically not updated after creation
    }
}

package com.springBoot.autoEcole.mapper;

import org.springframework.stereotype.Component;
import com.springBoot.autoEcole.model.Payment;
import com.springBoot.autoEcole.model.Candidate;

@Component
public class PaymentMapper {

    public Payment toEntity(Payment source, Candidate candidate) {
        if (source == null || candidate == null) {
            return null;
        }

        Payment target = new Payment();
        target.setPaidAmount(source.getPaidAmount());
        target.setStatus(source.getStatus());
        target.setTotalAmount(source.getTotalAmount());
        target.setCandidate(candidate);
        // Note: paymentTranches is ignored during creation

        return target;
    }

    public void updateEntity(Payment target, Payment source) {
        if (source == null || target == null) {
            return;
        }

        target.setPaidAmount(source.getPaidAmount());
        target.setStatus(source.getStatus());
        target.setTotalAmount(source.getTotalAmount());
        // Note: candidate and paymentTranches are ignored in update
    }
}
package com.autoecole.mapper;

import org.springframework.stereotype.Component;
import com.autoecole.model.Payment;
import com.autoecole.model.ApplicationFile;

@Component
public class PaymentMapper {

    public Payment toEntity(Payment source, ApplicationFile applicationFile) {
        if (source == null || applicationFile == null) {
            return null;
        }

        Payment target = new Payment();
        target.setPaidAmount(source.getPaidAmount());
        target.setStatus(source.getStatus());
        target.setTotalAmount(source.getTotalAmount());
        target.setApplicationFile(applicationFile);
        // Note: paymentInstallments is ignored during creation

        return target;
    }

    public void updateEntity(Payment target, Payment source) {
        if (source == null || target == null) {
            return;
        }

        target.setPaidAmount(source.getPaidAmount());
        target.setStatus(source.getStatus());
        target.setTotalAmount(source.getTotalAmount());
        // Note: applicationFile and paymentInstallments are ignored in update
    }
}
package com.springBoot.autoEcole.dto;

import com.springBoot.autoEcole.model.Payment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class PaymentWithInstallmentsDTO {

    private Long id;
    private Integer paidAmount;
    private String status;
    private Integer totalAmount;
    private List<PaymentInstallmentDTO> paymentInstallments;

    // Static factory method to create DTO from Payment entity
    public static PaymentWithInstallmentsDTO fromEntity(Payment payment) {
        if (payment == null) {
            return null;
        }

        return PaymentWithInstallmentsDTO.builder()
                .id(payment.getId())
                .paidAmount(payment.getPaidAmount())
                .status(payment.getStatus())
                .totalAmount(payment.getTotalAmount())
                .paymentInstallments(payment.getPaymentInstallments() != null ?
                        payment.getPaymentInstallments().stream()
                                .map(PaymentInstallmentDTO::fromEntity)
                                .collect(Collectors.toList()) : null)
                .build();
    }
}
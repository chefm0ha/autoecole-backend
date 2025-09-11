package com.autoecole.dto;

import com.autoecole.model.PaymentInstallment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class PaymentInstallmentDTO {

    private Long id;
    private Integer amount;
    private LocalDate date;
    private Integer installmentNumber;

    // Static factory method to create DTO from PaymentInstallment entity
    public static PaymentInstallmentDTO fromEntity(PaymentInstallment installment) {
        if (installment == null) {
            return null;
        }

        return PaymentInstallmentDTO.builder()
                .id(installment.getId())
                .amount(installment.getAmount())
                .date(installment.getDate())
                .installmentNumber(installment.getInstallmentNumber())
                .build();
    }
}
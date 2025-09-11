package com.autoecole.dto.response;

import com.autoecole.model.PaymentInstallment;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;

public record PaymentInstallmentDTO(
        @JsonProperty("id") Long id,
        @JsonProperty("amount") Integer amount,
        @JsonProperty("date") LocalDate date,
        @JsonProperty("installmentNumber") Integer installmentNumber,
        @JsonProperty("status") String status
) {
    public static PaymentInstallmentDTO fromEntity(PaymentInstallment installment) {
        if (installment == null) return null;

        return new PaymentInstallmentDTO(
                installment.getId(),
                installment.getAmount(),
                installment.getDate(),
                installment.getInstallmentNumber(),
                installment.getStatus() != null ? installment.getStatus().name() : null
        );
    }
}
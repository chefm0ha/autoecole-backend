package com.autoecole.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public enum PaymentInstallmentStatus {
    PENDING("Payment installment is pending validation"),
    VALIDATED("Payment installment has been validated");

    private final String description;
}
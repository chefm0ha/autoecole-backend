package com.autoecole.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public enum PaymentStatus {
    PENDING("Partial payment received"),
    COMPLETED("Payment completed in full");

    private final String description;
}
package com.autoecole.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public enum ExamType {
    THEORY("Written theory exam"),
    PRACTICAL("Practical driving exam");

    private final String description;
}
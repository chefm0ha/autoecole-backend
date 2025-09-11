package com.autoecole.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public enum SessionType {
    THEORY("Theory lesson in classroom"),
    PRACTICAL("Practical driving lesson");

    private final String description;
}
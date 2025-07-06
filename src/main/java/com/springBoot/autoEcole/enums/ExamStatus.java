package com.springBoot.autoEcole.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public enum ExamStatus {
    SCHEDULED("Exam is scheduled"),
    PASSED("Exam was passed successfully"),
    FAILED("Exam was failed");

    private final String description;
}
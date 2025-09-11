package com.autoecole.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public enum ApplicationFileStatus {
    IN_PROGRESS("Application file is in progress"),
    THEORY_EXAM_SCHEDULED("Theory exam has been scheduled"),
    PRACTICAL_EXAM_SCHEDULED("Practical exam has been scheduled"),
    THEORY_PASSED("Theory exam passed, awaiting practical exam"),
    THEORY_FAILED("Theory exam failed"),
    PRACTICAL_FAILED("Practical exam failed"),
    COMPLETED("Application file completed successfully"),
    CANCELLED("Application file was cancelled"),
    FAILED("Application file failed due to exam failures");

    private final String description;
}
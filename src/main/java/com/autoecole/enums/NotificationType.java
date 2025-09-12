package com.autoecole.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public enum NotificationType {
    EXAM_REMINDER("Exam reminder notification"),
    SYSTEM_ALERT("System alert notification");

    private final String description;
}

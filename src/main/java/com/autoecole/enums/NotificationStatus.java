package com.autoecole.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public enum NotificationStatus {
    PENDING("Notification is pending to be sent"),
    SENT("Notification has been sent successfully"),
    READ("Notification has been read by recipient"),
    FAILED("Notification failed to send");

    private final String description;
}
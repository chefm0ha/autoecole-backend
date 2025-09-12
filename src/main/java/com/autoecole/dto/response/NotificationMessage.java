package com.autoecole.dto.response.websocket;

import com.autoecole.model.Notification;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

/**
 * Modern Record-based NotificationMessage
 *
 * Benefits of using Record:
 * - Immutable by default
 * - Automatic equals(), hashCode(), toString()
 * - Compact syntax
 * - Better performance
 * - Clear intent (data carrier)
 */
public record NotificationMessage(
        @JsonProperty("id") Long id,
        @JsonProperty("title") String title,
        @JsonProperty("message") String message,
        @JsonProperty("type") String type,
        @JsonProperty("createdAt") LocalDateTime createdAt,
        @JsonProperty("examId") Long examId,
        @JsonProperty("examDate") String examDate,
        @JsonProperty("candidateName") String candidateName,
        @JsonProperty("categoryCode") String categoryCode
) {

    /**
     * Factory method to create NotificationMessage from Notification entity
     * Records can have static methods just like classes
     */
    public static NotificationMessage fromNotification(Notification notification) {
        return new NotificationMessage(
                notification.getId(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getType().name(),
                notification.getCreatedAt(),
                extractExamId(notification),
                extractExamDate(notification),
                extractCandidateName(notification),
                extractCategoryCode(notification)
        );
    }

    /**
     * Helper methods for safe navigation through object relationships
     * Records can have private static methods
     */
    private static Long extractExamId(Notification notification) {
        return notification.getExam() != null ? notification.getExam().getId() : null;
    }

    private static String extractExamDate(Notification notification) {
        return notification.getExam() != null ? notification.getExam().getDate().toString() : null;
    }

    private static String extractCandidateName(Notification notification) {
        if (notification.getExam() != null &&
                notification.getExam().getApplicationFile() != null &&
                notification.getExam().getApplicationFile().getCandidate() != null) {

            var candidate = notification.getExam().getApplicationFile().getCandidate();
            return candidate.getFirstName() + " " + candidate.getLastName();
        }
        return null;
    }

    private static String extractCategoryCode(Notification notification) {
        if (notification.getExam() != null &&
                notification.getExam().getApplicationFile() != null &&
                notification.getExam().getApplicationFile().getCategory() != null) {

            return notification.getExam().getApplicationFile().getCategory().getCode();
        }
        return null;
    }

    /**
     * Convenience methods (can be added to records)
     */
    public boolean isExamRelated() {
        return examId != null;
    }

    public boolean hasCandidate() {
        return candidateName != null && !candidateName.trim().isEmpty();
    }

    /**
     * Builder-style factory methods for different notification types
     */
    public static NotificationMessage examReminder(
            Long id, String title, String message, LocalDateTime createdAt,
            Long examId, String examDate, String candidateName, String categoryCode) {

        return new NotificationMessage(id, title, message, "EXAM_REMINDER",
                createdAt, examId, examDate, candidateName, categoryCode);
    }

    public static NotificationMessage paymentDue(Long id, String title, String message, LocalDateTime createdAt) {
        return new NotificationMessage(id, title, message, "PAYMENT_DUE",
                createdAt, null, null, null, null);
    }

    public static NotificationMessage systemAlert(Long id, String title, String message, LocalDateTime createdAt) {
        return new NotificationMessage(id, title, message, "SYSTEM_ALERT",
                createdAt, null, null, null, null);
    }
}
package com.autoecole.dto.response;

import com.autoecole.model.Notification;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public record NotificationDTO(
        @JsonProperty("id") Long id,
        @JsonProperty("title") String title,
        @JsonProperty("message") String message,
        @JsonProperty("type") String type,
        @JsonProperty("createdAt") LocalDateTime createdAt,
        @JsonProperty("sentAt") LocalDateTime sentAt,
        @JsonProperty("readAt") LocalDateTime readAt,
        @JsonProperty("examId") Long examId,
        @JsonProperty("examDate") String examDate,
        @JsonProperty("examType") String examType,
        @JsonProperty("candidateName") String candidateName,
        @JsonProperty("categoryCode") String categoryCode
) {
    public static NotificationDTO fromEntity(Notification notification) {
        if (notification == null) return null;

        return new NotificationDTO(
                notification.getId(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getType() != null ? notification.getType().name() : null,
                notification.getCreatedAt(),
                notification.getSentAt(),
                notification.getReadAt(),
                extractExamId(notification),
                extractExamDate(notification),
                extractExamType(notification),
                extractCandidateName(notification),
                extractCategoryCode(notification)
        );
    }

    private static Long extractExamId(Notification notification) {
        return notification.getExam() != null ? notification.getExam().getId() : null;
    }

    private static String extractExamDate(Notification notification) {
        return notification.getExam() != null ? notification.getExam().getDate().toString() : null;
    }

    private static String extractExamType(Notification notification) {
        return notification.getExam() != null && notification.getExam().getExamType() != null
                ? notification.getExam().getExamType().name() : null;
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
}
package com.autoecole.dto.response;

import com.autoecole.model.Exam;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;

public record ExamResponseDTO(
        @JsonProperty("id") Long id,
        @JsonProperty("examType") String examType,
        @JsonProperty("date") LocalDate date,
        @JsonProperty("status") String status,
        @JsonProperty("attemptNumber") Integer attemptNumber
) {
    public static ExamResponseDTO fromEntity(Exam exam) {
        if (exam == null) return null;

        return new ExamResponseDTO(
                exam.getId(),
                exam.getExamType() != null ? exam.getExamType().name() : null,
                exam.getDate(),
                exam.getStatus() != null ? exam.getStatus().name() : null,
                exam.getAttemptNumber()
        );
    }
}
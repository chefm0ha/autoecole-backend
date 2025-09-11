package com.autoecole.dto.response;

import com.autoecole.model.Exam;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public record CalendarExamDTO(
        @JsonProperty("id") Long id,
        @JsonProperty("examType") String examType,
        @JsonProperty("date") LocalDate date,
        @JsonProperty("status") String status,
        @JsonProperty("attemptNumber") Integer attemptNumber,
        @JsonProperty("candidateFirstName") String candidateFirstName,
        @JsonProperty("candidateLastName") String candidateLastName,
        @JsonProperty("candidateCin") String candidateCin,
        @JsonProperty("categoryCode") String categoryCode,
        @JsonProperty("applicationFileId") Long applicationFileId
) {
    public static CalendarExamDTO fromEntity(Exam exam) {
        if (exam == null) return null;

        return new CalendarExamDTO(
                exam.getId(),
                exam.getExamType() != null ? exam.getExamType().name() : null,
                exam.getDate(),
                exam.getStatus() != null ? exam.getStatus().name() : null,
                exam.getAttemptNumber(),
                exam.getApplicationFile() != null &&
                        exam.getApplicationFile().getCandidate() != null ?
                        exam.getApplicationFile().getCandidate().getFirstName() : null,
                exam.getApplicationFile() != null &&
                        exam.getApplicationFile().getCandidate() != null ?
                        exam.getApplicationFile().getCandidate().getLastName() : null,
                exam.getApplicationFile() != null &&
                        exam.getApplicationFile().getCandidate() != null ?
                        exam.getApplicationFile().getCandidate().getCin() : null,
                exam.getApplicationFile() != null &&
                        exam.getApplicationFile().getCategory() != null ?
                        exam.getApplicationFile().getCategory().getCode() : null,
                exam.getApplicationFile() != null ?
                        exam.getApplicationFile().getId() : null
        );
    }
}
package com.autoecole.dto.response;

import com.autoecole.model.Exam;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;

public record ComingExamDTO(
        @JsonProperty("id") Long id,
        @JsonProperty("examType") String examType,
        @JsonProperty("date") LocalDate date,
        @JsonProperty("status") String status,
        @JsonProperty("attemptNumber") Integer attemptNumber,
        @JsonProperty("candidateFullName") String candidateFullName,
        @JsonProperty("candidateCin") String candidateCin,
        @JsonProperty("vehicleImmatriculation") String vehicleImmatriculation,
        @JsonProperty("categoryCode") String categoryCode
) {
    public static ComingExamDTO fromEntity(Exam exam) {
        if (exam == null) return null;

        // Extract candidate information
        String candidateFullName = null;
        String candidateCin = null;
        String categoryCode = null;

        if (exam.getApplicationFile() != null && exam.getApplicationFile().getCandidate() != null) {
            var candidate = exam.getApplicationFile().getCandidate();
            candidateFullName = candidate.getFirstName() + " " + candidate.getLastName();
            candidateCin = candidate.getCin();
        }

        if (exam.getApplicationFile() != null && exam.getApplicationFile().getCategory() != null) {
            categoryCode = exam.getApplicationFile().getCategory().getCode();
        }

        // Extract vehicle immatriculation (only for practical exams)
        String vehicleImmatriculation = null;
        if (exam.getVehicle() != null) {
            vehicleImmatriculation = exam.getVehicle().getImmatriculation();
        }

        return new ComingExamDTO(
                exam.getId(),
                exam.getExamType() != null ? exam.getExamType().name() : null,
                exam.getDate(),
                exam.getStatus() != null ? exam.getStatus().name() : null,
                exam.getAttemptNumber(),
                candidateFullName,
                candidateCin,
                vehicleImmatriculation,
                categoryCode
        );
    }
}
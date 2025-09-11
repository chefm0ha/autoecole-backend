package com.autoecole.dto;

import com.autoecole.model.Exam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class CalendarExamDTO {

    private Long id;
    private String examType;
    private LocalDate date;
    private String status;
    private Integer attemptNumber;
    private String candidateFirstName;
    private String candidateLastName;
    private String candidateCin;
    private String categoryCode;
    private Long applicationFileId;

    // Static factory method to create DTO from Exam entity
    public static CalendarExamDTO fromEntity(Exam exam) {
        if (exam == null) {
            return null;
        }

        return CalendarExamDTO.builder()
                .id(exam.getId())
                .examType(exam.getExamType() != null ? exam.getExamType().name() : null)
                .date(exam.getDate())
                .status(exam.getStatus() != null ? exam.getStatus().name() : null)
                .attemptNumber(exam.getAttemptNumber())
                .candidateFirstName(exam.getApplicationFile() != null &&
                        exam.getApplicationFile().getCandidate() != null ?
                        exam.getApplicationFile().getCandidate().getFirstName() : null)
                .candidateLastName(exam.getApplicationFile() != null &&
                        exam.getApplicationFile().getCandidate() != null ?
                        exam.getApplicationFile().getCandidate().getLastName() : null)
                .candidateCin(exam.getApplicationFile() != null &&
                        exam.getApplicationFile().getCandidate() != null ?
                        exam.getApplicationFile().getCandidate().getCin() : null)
                .categoryCode(exam.getApplicationFile() != null &&
                        exam.getApplicationFile().getCategory() != null ?
                        exam.getApplicationFile().getCategory().getCode() : null)
                .applicationFileId(exam.getApplicationFile() != null ?
                        exam.getApplicationFile().getId() : null)
                .build();
    }

    // Helper method to get date as string in YYYY-MM-DD format
    public String getDateAsString() {
        return date != null ? date.toString() : null;
    }
}
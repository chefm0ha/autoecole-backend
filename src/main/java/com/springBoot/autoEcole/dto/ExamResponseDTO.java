package com.springBoot.autoEcole.dto;

import com.springBoot.autoEcole.model.Exam;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class ExamResponseDTO {
    private Long id;
    private String examType;
    private LocalDate date;
    private String status;
    private Integer attemptNumber;

    public static ExamResponseDTO fromEntity(Exam exam) {
        return ExamResponseDTO.builder()
                .id(exam.getId())
                .examType(exam.getExamType())
                .date(exam.getDate())
                .status(exam.getStatus())
                .attemptNumber(exam.getAttemptNumber())
                .build();
    }
}

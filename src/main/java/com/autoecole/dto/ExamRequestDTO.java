package com.autoecole.dto;

import lombok.*;

import java.time.LocalDate;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class ExamRequestDTO {
    private String examType; // "THEORY" or "PRACTICAL"
    private LocalDate date;
    private String status; // "SCHEDULED", "PASSED", "FAILED"
    private String immatriculation;
}

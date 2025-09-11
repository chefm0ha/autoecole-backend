package com.autoecole.dto;

import com.autoecole.model.ApplicationFile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class ApplicationFileDTO {

    private Long id;
    private Double practicalHoursCompleted;
    private Double theoreticalHoursCompleted;
    private Boolean isActive;
    private LocalDate startingDate;
    private String status;
    private String fileNumber;
    private String taxStamp;
    private String medicalVisit;
    private String categoryCode;

    // Static factory method to create DTO from ApplicationFile entity
    public static ApplicationFileDTO fromEntity(ApplicationFile applicationFile) {
        if (applicationFile == null) {
            return null;
        }

        return ApplicationFileDTO.builder()
                .id(applicationFile.getId())
                .practicalHoursCompleted(applicationFile.getPracticalHoursCompleted())
                .theoreticalHoursCompleted(applicationFile.getTheoreticalHoursCompleted())
                .isActive(applicationFile.getIsActive())
                .startingDate(applicationFile.getStartingDate())
                .status(applicationFile.getStatus() != null ? applicationFile.getStatus().name() : null)
                .fileNumber(applicationFile.getFileNumber())
                .taxStamp(applicationFile.getTaxStamp() != null ? applicationFile.getTaxStamp().name() : null)
                .medicalVisit(applicationFile.getMedicalVisit() != null ? applicationFile.getMedicalVisit().name() : null)
                .categoryCode(applicationFile.getCategory() != null ?
                        applicationFile.getCategory().getCode() : null)
                .build();
    }
}
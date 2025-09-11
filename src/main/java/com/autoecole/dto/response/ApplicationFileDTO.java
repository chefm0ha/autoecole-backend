package com.autoecole.dto.response;

import com.autoecole.model.ApplicationFile;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public record ApplicationFileDTO (
        @JsonProperty("id") Long id,
        @JsonProperty("practicalHoursCompleted") Double practicalHoursCompleted,
        @JsonProperty("theoreticalHoursCompleted") Double theoreticalHoursCompleted,
        @JsonProperty("isActive") Boolean isActive,
        @JsonProperty("startingDate") LocalDate startingDate,
        @JsonProperty("status") String status,
        @JsonProperty("fileNumber") String fileNumber,
        @JsonProperty("taxStamp") String taxStamp,
        @JsonProperty("medicalVisit") String medicalVisit,
        @JsonProperty("categoryCode") String categoryCode
) {
    public static ApplicationFileDTO fromEntity(ApplicationFile applicationFile) {
        if (applicationFile == null) return null;

        return new ApplicationFileDTO(
                applicationFile.getId(),
                applicationFile.getPracticalHoursCompleted(),
                applicationFile.getTheoreticalHoursCompleted(),
                applicationFile.getIsActive(),
                applicationFile.getStartingDate(),
                applicationFile.getStatus() != null ? applicationFile.getStatus().name() : null,
                applicationFile.getFileNumber(),
                applicationFile.getTaxStamp() != null ? applicationFile.getTaxStamp().name() : null,
                applicationFile.getMedicalVisit() != null ? applicationFile.getMedicalVisit().name() : null,
                applicationFile.getCategory() != null ? applicationFile.getCategory().getCode() : null
        );
    }
}
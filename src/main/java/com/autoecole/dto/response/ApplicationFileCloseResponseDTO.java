package com.autoecole.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class ApplicationFileCloseResponseDTO {
    private String message;
    private String status;
    private String reason;
    private Boolean isEligible;

    public static ApplicationFileCloseResponseDTO completed() {
        return ApplicationFileCloseResponseDTO.builder()
                .message("Application file completed successfully")
                .status("COMPLETED")
                .isEligible(true)
                .build();
    }

    public static ApplicationFileCloseResponseDTO cancelled(String reason) {
        return ApplicationFileCloseResponseDTO.builder()
                .message("Application file cancelled - not eligible for completion")
                .status("CANCELLED")
                .reason(reason)
                .isEligible(false)
                .build();
    }
}
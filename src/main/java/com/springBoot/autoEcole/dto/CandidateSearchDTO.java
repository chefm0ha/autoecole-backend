package com.springBoot.autoEcole.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class CandidateSearchDTO {

    private String firstName;
    private String lastName;
    private String cin;
    private Boolean isActive;

    // Helper method to check if any search criteria is provided
    public boolean hasSearchCriteria() {
        return (firstName != null && !firstName.trim().isEmpty()) ||
                (lastName != null && !lastName.trim().isEmpty()) ||
                (cin != null && !cin.trim().isEmpty()) ||
                isActive != null;
    }
}
package com.autoecole.dto;

import com.autoecole.model.Candidate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class CandidateListDTO {

    private String cin;
    private String firstName;
    private String lastName;
    private LocalDate birthDay;
    private Boolean isActive;
    private String gsm;
    private LocalDate startingDate;

    // Static factory method to create DTO from Candidate entity
    public static CandidateListDTO fromEntity(Candidate candidate) {
        if (candidate == null) {
            return null;
        }

        return CandidateListDTO.builder()
                .cin(candidate.getCin())
                .firstName(candidate.getFirstName())
                .lastName(candidate.getLastName())
                .birthDay(candidate.getBirthDay())
                .isActive(candidate.getIsActive())
                .gsm(candidate.getGsm())
                .startingDate(candidate.getStartingDate())
                .build();
    }
}
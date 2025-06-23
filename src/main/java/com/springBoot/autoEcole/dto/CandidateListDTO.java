package com.springBoot.autoEcole.dto;

import com.springBoot.autoEcole.model.Candidate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class CandidateListDTO {

    private String cin;
    private String gender;
    private String firstName;
    private String lastName;
    private LocalDate birthDay;
    private String birthPlace;
    private Boolean isActive;
    private String city;
    private String address;
    private String email;
    private String gsm;
    private LocalDate startingDate;

    // Static factory method to create DTO from Candidate entity
    public static CandidateListDTO fromEntity(Candidate candidate) {
        if (candidate == null) {
            return null;
        }

        return CandidateListDTO.builder()
                .cin(candidate.getCin())
                .gender(candidate.getGender())
                .firstName(candidate.getFirstName())
                .lastName(candidate.getLastName())
                .birthDay(candidate.getBirthDay())
                .birthPlace(candidate.getBirthPlace())
                .isActive(candidate.getIsActive())
                .city(candidate.getCity())
                .address(candidate.getAddress())
                .email(candidate.getEmail())
                .gsm(candidate.getGsm())
                .startingDate(candidate.getStartingDate())
                .build();
    }
}
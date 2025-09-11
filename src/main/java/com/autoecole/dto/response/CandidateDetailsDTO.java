package com.autoecole.dto.response;

import com.autoecole.model.Candidate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class CandidateDetailsDTO {

    // Basic candidate information
    private String cin;
    private String firstName;
    private String lastName;
    private LocalDate birthDay;
    private String birthPlace;
    private String address;
    private String city;
    private String email;
    private String gender;
    private String gsm;
    private Boolean isActive;
    private LocalDate startingDate;



    // Static factory method to create DTO from Candidate entity
    public static CandidateDetailsDTO fromEntity(Candidate candidate) {
        if (candidate == null) {
            return null;
        }

        return CandidateDetailsDTO.builder()
                .cin(candidate.getCin())
                .firstName(candidate.getFirstName())
                .lastName(candidate.getLastName())
                .birthDay(candidate.getBirthDay())
                .birthPlace(candidate.getBirthPlace())
                .address(candidate.getAddress())
                .city(candidate.getCity())
                .email(candidate.getEmail())
                .gender(candidate.getGender() != null ? candidate.getGender().name() : null)
                .gsm(candidate.getGsm())
                .isActive(candidate.getIsActive())
                .startingDate(candidate.getStartingDate())
                .build();
    }
}
package com.springBoot.autoEcole.dto;

import com.springBoot.autoEcole.model.ApplicationFile;
import com.springBoot.autoEcole.model.Candidate;
import com.springBoot.autoEcole.model.Session;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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

    // Related entities IDs - Updated to reflect new schema
    private List<Long> applicationFileIds;
    private List<Long> sessionIds;
    private List<Long> paymentIds; // Now multiple payments (one per application file)

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
                .gender(candidate.getGender())
                .gsm(candidate.getGsm())
                .isActive(candidate.getIsActive())
                .startingDate(candidate.getStartingDate())
                .applicationFileIds(candidate.getApplicationFiles() != null ?
                        candidate.getApplicationFiles().stream()
                                .map(ApplicationFile::getId)
                                .collect(Collectors.toList()) : null)
                .sessionIds(candidate.getSessions() != null ?
                        candidate.getSessions().stream()
                                .map(Session::getId)
                                .collect(Collectors.toList()) : null)
                .paymentIds(candidate.getApplicationFiles() != null ?
                        candidate.getApplicationFiles().stream()
                                .filter(af -> af.getPayment() != null)
                                .map(af -> af.getPayment().getId())
                                .collect(Collectors.toList()) : null)
                .build();
    }
}
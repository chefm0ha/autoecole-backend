package com.autoecole.dto.response;

import com.autoecole.model.Candidate;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;

public record CandidateListDTO(
        @JsonProperty("cin") String cin,
        @JsonProperty("firstName") String firstName,
        @JsonProperty("lastName") String lastName,
        @JsonProperty("birthDay") LocalDate birthDay,
        @JsonProperty("isActive") Boolean isActive,
        @JsonProperty("gsm") String gsm,
        @JsonProperty("startingDate") LocalDate startingDate
) {
    public static CandidateListDTO fromEntity(Candidate candidate) {
        if (candidate == null) return null;

        return new CandidateListDTO(
                candidate.getCin(),
                candidate.getFirstName(),
                candidate.getLastName(),
                candidate.getBirthDay(),
                candidate.getIsActive(),
                candidate.getGsm(),
                candidate.getStartingDate()
        );
    }
}
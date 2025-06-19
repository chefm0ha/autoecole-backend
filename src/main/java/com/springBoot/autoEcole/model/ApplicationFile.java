package com.springBoot.autoEcole.model;

import java.time.LocalDate;
import javax.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "application_file")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class ApplicationFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "practical_hours_completed")
    private Double practicalHoursCompleted;

    @Column(name = "theoretical_hours_completed")
    private Double theoreticalHoursCompleted;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "starting_date")
    private LocalDate startingDate;

    @Column(name = "status")
    private String status;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_cin")
    private Candidate candidate;
}
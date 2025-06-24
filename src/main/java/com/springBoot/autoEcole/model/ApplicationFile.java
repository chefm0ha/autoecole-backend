package com.springBoot.autoEcole.model;

import java.time.LocalDate;
import java.util.List;
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

    @Column(name = "file_number")
    private String fileNumber;

    @Column(name = "tax_stamp")
    private Boolean taxStamp;

    @Column(name = "medical_visit")
    private String medicalVisit;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_code")
    private Category category;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_cin")
    private Candidate candidate;

    @OneToMany(mappedBy = "applicationFile", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Exam> exams;

    @OneToOne(mappedBy = "applicationFile", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Payment payment;
}
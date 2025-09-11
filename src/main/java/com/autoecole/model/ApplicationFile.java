package com.autoecole.model;

import java.time.LocalDate;
import java.util.List;
import javax.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.autoecole.enums.ApplicationFileStatus;
import com.autoecole.enums.TaxStampStatus;
import com.autoecole.enums.MedicalVisitStatus;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ApplicationFileStatus status;

    @Column(name = "file_number")
    private String fileNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "tax_stamp")
    private TaxStampStatus taxStamp;

    @Enumerated(EnumType.STRING)
    @Column(name = "medical_visit")
    private MedicalVisitStatus medicalVisit;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_code")
    private Category category;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_cin")
    private Candidate candidate;

    @OneToMany(mappedBy = "applicationFile", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Exam> exams;

    @OneToOne(mappedBy = "applicationFile", cascade = CascadeType.ALL)
    private Payment payment;
}
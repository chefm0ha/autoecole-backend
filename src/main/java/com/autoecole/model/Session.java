package com.autoecole.model;

import java.time.LocalDate;
import javax.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.autoecole.enums.SessionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "session")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "date_session")
    private LocalDate dateSession;

    @Column(name = "duration")
    private Double duration;

    @Column(name = "status")
    private String status;

    @Enumerated(EnumType.STRING)
    @Column(name = "session_type")
    private SessionType sessionType;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_cin")
    private Candidate candidate;

    @ManyToOne
    @JoinColumn(name = "instructor_cin")
    private Instructor instructor;

    @ManyToOne
    @JoinColumn(name = "vehicle_immat")
    private Vehicle vehicle;
}
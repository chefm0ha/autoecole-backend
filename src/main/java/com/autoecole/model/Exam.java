package com.autoecole.model;

import java.time.LocalDate;
import javax.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.autoecole.enums.ExamType;
import com.autoecole.enums.ExamStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "exam")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Exam {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "attempt_number")
	private Integer attemptNumber;

	@Column(name = "date")
	private LocalDate date;

	@Enumerated(EnumType.STRING)
	@Column(name = "exam_type")
	private ExamType examType;

	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private ExamStatus status;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "application_file_id")
	private ApplicationFile applicationFile;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "vehicle_id")
	private Vehicle vehicle;
}

package com.autoecole.model;

import java.time.LocalDate;
import javax.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "insurance")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Insurance {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "amount")
	private Integer amount;

	@Column(name = "company")
	private String company;

	@Column(name = "next_operation_date")
	private LocalDate nextOperationDate;

	@Column(name = "operation_date")
	private LocalDate operationDate;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "vehicle_immat")
	private Vehicle vehicle;
}
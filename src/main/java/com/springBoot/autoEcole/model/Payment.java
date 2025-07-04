package com.springBoot.autoEcole.model;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "payment")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Payment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "paid_amount")
	private Integer paidAmount;

	@Column(name = "status")
	private String status;

	@Column(name = "total_amount")
	private Integer totalAmount;

	@JsonIgnore
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "application_file_id", unique = true)
	private ApplicationFile applicationFile;

	@OneToMany(mappedBy = "payment", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<PaymentInstallment> paymentInstallments;
}

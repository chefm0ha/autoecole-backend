package com.autoecole.model;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.autoecole.enums.PaymentStatus;

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

	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private PaymentStatus status;

	@Column(name = "total_amount")
	private Integer totalAmount;

	@JsonIgnore
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "application_file_id", unique = true)
	private ApplicationFile applicationFile;

	@OneToMany(mappedBy = "payment", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<PaymentInstallment> paymentInstallments;

	public static Payment createNew(ApplicationFile applicationFile, Integer totalAmount) {
		return Payment.builder()
				.applicationFile(applicationFile)
				.paidAmount(0)
				.status(PaymentStatus.PENDING)
				.totalAmount(totalAmount)
				.build();
	}
}

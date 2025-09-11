package com.autoecole.model;


import java.time.LocalDate;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.autoecole.enums.FuelType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "vehicle")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Vehicle {

	@Id
	@Column(name = "immatriculation")
	private String immatriculation;

	@Column(name = "amount_vignette")
	private Double amountVignette;

	@Column(name = "category")
	private String category;

	@Column(name = "date_last_vignette")
	private LocalDate dateLastVignette;

	@Column(name = "km_initial")
	private Integer kmInitial;

	@Column(name = "vehicle_brand")
	private String vehicleBrand;

	@Enumerated(EnumType.STRING)
	@Column(name = "fuel_type")
	private FuelType fuelType;

	@Column(name = "vehicle_type")
	private String vehicleType;

	@OneToMany(mappedBy = "vehicle", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<Insurance> insurances;

	@OneToMany(mappedBy = "vehicle", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<OilChange> oilChanges;

	@OneToMany(mappedBy = "vehicle", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<TechnicalVisit> technicalVisits;

	@Column(name = "quota")
	private int quota;
}

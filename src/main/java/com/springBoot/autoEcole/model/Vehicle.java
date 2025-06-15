package com.springBoot.autoEcole.model;


import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name ="VEHICLE")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Vehicle {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name ="IMMATRICULATION")
	private String immatriculation;
	
	@Column(name ="MARQUE_VEHICLE")
	private String marqueVehicle;
	
	@Column(name ="TYPE_VEHICLE")
	private String typeVehicle;
	
	@Column(name ="TYPE_CARBURANT")
	private String typeCarburant;
	
	@Column(name ="CATEGORY")
	private String category;
	
	@Column(name ="KMINITIAL")
	private Integer kmInitial;
	
	@Column(name ="AMOUNT_VIGNETTE")
	private Integer AmoutVignette;
	
	@Column(name ="DATE_LAST_VIGNETTE")
	private Date DateLastVignette;
	
	@OneToMany( mappedBy = "vehicle" ,fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<OilChange> oilChange;
	
	@OneToMany( mappedBy = "vehicle" ,fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<TechnicalVisit> technicalVisit;
	
	@OneToMany( mappedBy = "vehicle" ,fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<Assurance> assurance;
}

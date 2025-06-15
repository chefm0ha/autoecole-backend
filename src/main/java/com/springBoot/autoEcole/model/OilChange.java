package com.springBoot.autoEcole.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name ="OIL_CHANGE")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class OilChange {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name ="ID")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(name ="AMOUNT")
	private Integer amount;
	
	@Column(name ="ACTUAL_KM")
	private Integer actualKm;
	
	@Column(name ="OPERATION_DATE")
	private Date operationDate;
	
	@Column(name ="NEXT_OPERATION_DATE")
	private Date nextOperationDate;
	
	@Column(name ="SOCIETY")
	private String society;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "IMMATRICULATION", insertable = true, updatable = false)
	private Vehicle vehicle; 
}

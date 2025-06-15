package com.springBoot.autoEcole.model;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@Table(name ="CANDIDAT")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Candidat{

	@Id
	@Column(name ="ID")
	private String id;

	
	@Column(name ="CIN")
	private String cin;
	
	@Column(name = "CATEGORY")
	private String category;
	
	@Column(name ="SEX")
	private String sex;
	
	@Column(name ="ACTIF")
	private Boolean actif;
	
	@Column(name ="INITIALPRICE", columnDefinition = "integer default 0")
	private Integer initialPrice;
	
	@Column(name = "FIRSTNAME")
	private String firstName;

	@Column(name = "LASTNAME")
	private String lastName;
	
	@Column(name = "BIRTHDAY")
	private Date birthday;
	
	@Column(name = "PLACEBIRTH")
	private String placeBirth;
	
	@Column(name = "ADDRESS")
	private String adress;
	
	@Column(name = "CITY")
	private String city;
	
	@Column(name = "GSM")
	private String gsm;

	@Column(name = "EMAIL")
	private String email;
	
	@Column(name = "STARTINGDATE")
	private Date startingDate;
	
	@OneToOne
	@JoinColumn(name ="VEHICLE")
	private Vehicle vehicle;
	
	@OneToOne
	@JoinColumn(name ="MONITOR")
	private Monitor monitor;
	
	@OneToMany( mappedBy = "candidat" ,fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<Payment> payment;
	
	@OneToMany( mappedBy = "candidat" ,fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<Exam> exam;
	
	@OneToMany( mappedBy = "candidat" ,fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<TrainingExam> trainingExam;
	
	@OneToMany( mappedBy = "candidat" ,fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<DrivingSession> drivingSession;
	

}

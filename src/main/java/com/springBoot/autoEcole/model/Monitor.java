package com.springBoot.autoEcole.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name ="MONITOR")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Monitor {
	@Id
	@Column(name ="CIN")
	private String cin;

	@Column(name = "FIRSTNAME")
	private String firstName;

	@Column(name = "LASTNAME")
	private String lastName;
	
	@Column(name = "BIRTHDAY")
	private Date birthday;
	
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
}

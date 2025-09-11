package com.autoecole.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "instructor")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Instructor {

	@Id
	@Column(name = "cin")
	private String cin;

	@Column(name = "address")
	private String address;

	@Column(name = "birthday")
	private LocalDate birthday;

	@Column(name = "city")
	private String city;

	@Column(name = "email")
	private String email;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "gsm")
	private String gsm;

	@Column(name = "last_name")
	private String lastName;

	@Column(name = "starting_date")
	private LocalDate startingDate;
}

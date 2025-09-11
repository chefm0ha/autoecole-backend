package com.autoecole.model;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.*;

import com.autoecole.enums.Gender;
import lombok.*;

@Entity
@Table(name = "candidate")
@Data
public class Candidate {

	@Id
	@Column(name = "cin")
	private String cin;

	@Column(name = "address")
	private String address;

	@Column(name = "birth_day")
	private LocalDate birthDay;

	@Column(name = "birth_place")
	private String birthPlace;

	@Column(name = "city")
	private String city;

	@Column(name = "email")
	private String email;

	@Column(name = "first_name")
	private String firstName;

	@Enumerated(EnumType.STRING)
	@Column(name = "gender")
	private Gender gender;

	@Column(name = "gsm")
	private String gsm;

	@Column(name = "is_active")
	private Boolean isActive;

	@Column(name = "last_name")
	private String lastName;

	@Column(name = "starting_date")
	private LocalDate startingDate;

	@OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL)
	private List<ApplicationFile> applicationFiles;

	@OneToMany(mappedBy = "candidate", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<Session> sessions;
}

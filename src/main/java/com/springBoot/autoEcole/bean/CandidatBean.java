package com.springBoot.autoEcole.bean;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class CandidatBean {

	private String cin;
	private String sex;
	private String firstName;
	private String lastName;
	private String birthday;
	private String placeBirth;
	private String adress;
	private String city;
	private String gsm;
	private String email;
	private String category;
	private String startingDate;
	private String exam1Date;
	private String exam1Score;
	private String exam2Date;
	private String exam2Score;
}


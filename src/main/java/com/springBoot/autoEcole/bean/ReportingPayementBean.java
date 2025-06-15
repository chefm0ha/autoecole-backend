package com.springBoot.autoEcole.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class ReportingPayementBean {

	private String fullName;
	private Integer initialPrice;
	private Integer paid;
	private Integer rest;
}

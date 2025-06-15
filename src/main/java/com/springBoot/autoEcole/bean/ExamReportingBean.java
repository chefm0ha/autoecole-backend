package com.springBoot.autoEcole.bean;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class ExamReportingBean {
	
	private String fullName;
	private String category;
	private String typeExam;
	private Date dateExam;
}

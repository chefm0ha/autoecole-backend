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
@Table(name ="TRAINING_EXAM")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class TrainingExam {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name ="ID")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(name ="NUM_SERIE")
	private Integer numSerie;
	
	@Column(name ="DATE_Training")
	private Date dateTraining;
	
	@Column(name ="SCORE")
	private Integer score;
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CANDIDATID",referencedColumnName="ID", insertable = true, updatable = false)
	private Candidat candidat; 
	
	
	

}

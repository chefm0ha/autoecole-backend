package com.springBoot.autoEcole.web.rest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springBoot.autoEcole.service.TrainingExamService;
import com.sun.istack.NotNull;

@RestController
@RequestMapping("/trainingExam")
@CrossOrigin
public class TrainingExamFacade {

	@Autowired
	private TrainingExamService trainingExamService;
	
	@PostMapping("/saveTrainingExam/{candidatId}") 
	public TrainingExam saveTrainingExam(@PathVariable String candidatId, @RequestBody TrainingExam test) {
		return trainingExamService.saveTrainingExam(candidatId,test);			
	}
	@GetMapping("/getLastTrainingsByCandidat/{candidatId}") 
	public Collection<TrainingExam> getLastTrainingsByCandidat(@PathVariable @NotNull String candidatId) {
		  ArrayList<TrainingExam> trainingExamSorted =new ArrayList<>(trainingExamService.getLastTrainingsByCandidat(candidatId)); 
		  Collections.reverse(trainingExamSorted);
		  return trainingExamSorted;
	}
}

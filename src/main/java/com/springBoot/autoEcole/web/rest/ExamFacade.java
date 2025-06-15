package com.springBoot.autoEcole.web.rest;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springBoot.autoEcole.bean.ExamReportingBean;
import com.springBoot.autoEcole.model.Exam;
import com.springBoot.autoEcole.service.ExamService;
import com.sun.istack.NotNull;

@RestController
@RequestMapping("/exam")
@CrossOrigin
public class ExamFacade {
	@Autowired
	private ExamService examService;
	
	@PostMapping("/saveExam/{candidatId}") 
	public Exam saveExam(@PathVariable String candidatId, @RequestBody Exam exam) {
		return examService.saveExam(candidatId,exam);			
	}
	
	@GetMapping("/deleteExam/{id}") 
	public Long deleteExam(@PathVariable @NotNull Long id) {
		return  examService.deleteExam(id);			
	}
	
	@GetMapping("/getExamOnPeriod/{period}") 
	public Collection<ExamReportingBean> getExamOnPeriod(@PathVariable @NotNull Integer period) {
		return  examService.getExamOnPeriod(period);			
	}
	
	@GetMapping("/getCountExamOnWeekByType/{typeExam}") 
	public Integer getCountExamOnWeekByType(@PathVariable @NotNull String typeExam) {
		return  examService.getCountExamOnWeekByType(typeExam);			
	}
	
	@GetMapping("/getSuccessRateExamCode") 
	public Float getSuccessRateExamCode() {
		return  examService.getSuccessRateExamCode();			
	}
	@GetMapping("/getSuccessRateExamConduite") 
	public Float getSuccessRateExamConduite() {
		return  examService.getSuccessRateExamConduite();			
	}
}

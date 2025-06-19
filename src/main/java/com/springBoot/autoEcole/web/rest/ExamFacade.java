package com.springBoot.autoEcole.web.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.springBoot.autoEcole.model.Exam;
import com.springBoot.autoEcole.service.ExamService;

@RestController
@RequestMapping("/exam")
@CrossOrigin
public class ExamFacade {

	@Autowired
	private ExamService examService;

	@PostMapping("/saveExam/{candidateCin}")
	public Exam saveExam(@PathVariable String candidateCin, @RequestBody Exam exam) {
		return examService.saveExam(candidateCin, exam);
	}

	@GetMapping("/deleteExam/{id}")
	public Long deleteExam(@PathVariable Long id) {
		return examService.deleteExam(id);
	}
}
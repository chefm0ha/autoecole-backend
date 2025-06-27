package com.springBoot.autoEcole.web.rest;

import com.springBoot.autoEcole.dto.ExamRequestDTO;
import com.springBoot.autoEcole.dto.ExamResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.springBoot.autoEcole.model.Exam;
import com.springBoot.autoEcole.service.ExamService;

import java.util.List;

@RestController
@RequestMapping("/exam")
@CrossOrigin
public class ExamFacade {

	@Autowired
	private ExamService examService;

	@PostMapping("/saveExam/{applicationFileId}")
	public ResponseEntity<?> saveExam(@PathVariable Long applicationFileId, @RequestBody ExamRequestDTO examRequest) {
		try {
			Exam savedExam = examService.saveExam(applicationFileId, examRequest);
			return ResponseEntity.ok("Exam saved successfully");
		} catch (IllegalStateException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Error saving exam: " + e.getMessage());
		}
	}

	@GetMapping("/getExamsByApplicationFile/{applicationFileId}")
	public ResponseEntity<List<ExamResponseDTO>> getExamsByApplicationFile(@PathVariable Long applicationFileId) {
		try {
			List<ExamResponseDTO> exams = examService.getExamsByApplicationFile(applicationFileId);
			return ResponseEntity.ok(exams);
		} catch (Exception e) {
			return ResponseEntity.status(500).body(null);
		}
	}
}
package com.springBoot.autoEcole.web.rest;

import com.springBoot.autoEcole.dto.ExamRequestDTO;
import com.springBoot.autoEcole.dto.ExamResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.springBoot.autoEcole.model.Exam;
import com.springBoot.autoEcole.service.ExamService;

import javax.persistence.EntityNotFoundException;
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
			// These are our business rule violations
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (EntityNotFoundException e) {
			// Application file not found
			return ResponseEntity.notFound().build();
		} catch (Exception e) {
			// Any other unexpected errors
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

	@PutMapping("/updateExamStatus/{examId}")
	public ResponseEntity<?> updateExamStatus(
			@PathVariable Long examId,
			@RequestParam String status) {
		try {
			Exam updatedExam = examService.updateExamStatus(examId, status);
			return ResponseEntity.ok("Exam status updated successfully");
		} catch (IllegalArgumentException e) {
			// Invalid input parameters
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (IllegalStateException e) {
			// Business rule violations
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (EntityNotFoundException e) {
			// Exam not found
			return ResponseEntity.notFound().build();
		} catch (Exception e) {
			// Any other unexpected errors
			return ResponseEntity.status(500).body("Error updating exam status: " + e.getMessage());
		}
	}
}
package com.springBoot.autoEcole.web.rest;

import com.springBoot.autoEcole.dto.CalendarExamDTO;
import com.springBoot.autoEcole.dto.ExamRequestDTO;
import com.springBoot.autoEcole.dto.ExamResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.springBoot.autoEcole.model.Exam;
import com.springBoot.autoEcole.service.ExamService;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
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

	// ==================== CALENDAR ENDPOINTS ====================

	/**
	 * Get exams for a specific month
	 * GET /exam/getExamsByMonth?year=2025&month=7
	 */
	@GetMapping("/getExamsByMonth")
	public ResponseEntity<List<CalendarExamDTO>> getExamsByMonth(
			@RequestParam int year,
			@RequestParam int month) {
		try {
			List<CalendarExamDTO> exams = examService.getExamsByMonth(year, month);
			return ResponseEntity.ok(exams);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(null);
		} catch (Exception e) {
			return ResponseEntity.status(500).body(null);
		}
	}

	/**
	 * Get exams for a specific date
	 * GET /exam/getExamsByDate?date=2025-07-15
	 */
	@GetMapping("/getExamsByDate")
	public ResponseEntity<List<CalendarExamDTO>> getExamsByDate(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
		try {
			List<CalendarExamDTO> exams = examService.getExamsByDate(date);
			return ResponseEntity.ok(exams);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(null);
		} catch (Exception e) {
			return ResponseEntity.status(500).body(null);
		}
	}

	/**
	 * Get exams for a date range
	 * GET /exam/getExamsByDateRange?startDate=2025-07-01&endDate=2025-07-31
	 */
	@GetMapping("/getExamsByDateRange")
	public ResponseEntity<List<CalendarExamDTO>> getExamsByDateRange(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
		try {
			List<CalendarExamDTO> exams = examService.getExamsByDateRange(startDate, endDate);
			return ResponseEntity.ok(exams);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(null);
		} catch (Exception e) {
			return ResponseEntity.status(500).body(null);
		}
	}

	/**
	 * Get all scheduled exams from a specific date onwards (useful for upcoming exams)
	 * GET /exam/getScheduledExams?fromDate=2025-07-01
	 * GET /exam/getScheduledExams (defaults to today)
	 */
	@GetMapping("/getScheduledExams")
	public ResponseEntity<List<CalendarExamDTO>> getScheduledExamsFromDate(
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate) {
		try {
			List<CalendarExamDTO> exams = examService.getScheduledExamsFromDate(fromDate);
			return ResponseEntity.ok(exams);
		} catch (Exception e) {
			return ResponseEntity.status(500).body(null);
		}
	}

	/**
	 * Get today's exams (convenience endpoint)
	 * GET /exam/getTodaysExams
	 */
	@GetMapping("/getTodaysExams")
	public ResponseEntity<List<CalendarExamDTO>> getTodaysExams() {
		try {
			List<CalendarExamDTO> exams = examService.getExamsByDate(LocalDate.now());
			return ResponseEntity.ok(exams);
		} catch (Exception e) {
			return ResponseEntity.status(500).body(null);
		}
	}

	/**
	 * Get this week's exams (convenience endpoint)
	 * GET /exam/getThisWeeksExams
	 */
	@GetMapping("/getThisWeeksExams")
	public ResponseEntity<List<CalendarExamDTO>> getThisWeeksExams() {
		try {
			LocalDate today = LocalDate.now();
			LocalDate startOfWeek = today.minusDays(today.getDayOfWeek().getValue() - 1); // Monday
			LocalDate endOfWeek = startOfWeek.plusDays(6); // Sunday

			List<CalendarExamDTO> exams = examService.getExamsByDateRange(startOfWeek, endOfWeek);
			return ResponseEntity.ok(exams);
		} catch (Exception e) {
			return ResponseEntity.status(500).body(null);
		}
	}
}
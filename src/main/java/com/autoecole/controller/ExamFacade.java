package com.autoecole.controller;

import com.autoecole.dto.response.CalendarExamDTO;
import com.autoecole.dto.request.ExamRequestDTO;
import com.autoecole.dto.response.ExamResponseDTO;
import com.autoecole.enums.ExamType;
import com.autoecole.model.Exam;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.autoecole.service.ExamService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/exam")
@CrossOrigin
@AllArgsConstructor
public class ExamFacade {

	private final ExamService examService;

	@PostMapping("/saveExam/{applicationFileId}")
	public ResponseEntity<String> saveExam(@PathVariable Long applicationFileId, @RequestBody ExamRequestDTO examRequest) {
		examService.saveExam(applicationFileId, examRequest);
		return ResponseEntity.ok("Exam saved successfully");
	}

	@GetMapping("/getExamsByApplicationFile/{applicationFileId}")
	public List<ExamResponseDTO> getExamsByApplicationFile(@PathVariable Long applicationFileId) {
		return examService.getExamsByApplicationFile(applicationFileId);
	}

	@PutMapping("/updateExamStatus/{examId}")
	public ResponseEntity<String> updateExamStatus(
			@PathVariable Long examId,
			@RequestParam String status) {
		examService.updateExamStatus(examId, status);
		return ResponseEntity.ok("Exam status updated successfully");
	}

	// ==================== CALENDAR ENDPOINTS ====================

	/**
	 * Get exams for a specific month
	 * GET /exam/getExamsByMonth?year=2025&month=7
	 */
	@GetMapping("/getExamsByMonth")
	public List<CalendarExamDTO> getExamsByMonth(
			@RequestParam int year,
			@RequestParam int month) {
		return examService.getExamsByMonth(year, month);
	}

	/**
	 * Get exams for a specific date
	 * GET /exam/getExamsByDate?date=2025-07-15
	 */
	@GetMapping("/getExamsByDate")
	public List<CalendarExamDTO> getExamsByDate(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
		return examService.getExamsByDate(date);
	}

	@GetMapping("/getScheduledExamsThisWeek")
	public List<Exam> getScheduledExamsThisWeek() { return examService.getScheduledExamsThisWeek(); }

	@GetMapping("/getComingExams")
	public List<ExamResponseDTO> getComingExams(@RequestParam(defaultValue = "10") int size) {
		return examService.getComingExams(size);
	}

	@GetMapping("/getPassedExams/{examType}")
	public int getPassedExamsByExamType(@PathVariable ExamType examType,
										@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
										@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate)
	{ return examService.getPassedExamsByExamType(examType, startDate, endDate); }

	@GetMapping("/getTotalExams/{examType}")
	public int getTotalExamsByExamType(@PathVariable ExamType examType,
									   @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
									   @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate)
	{ return examService.getTotalExamsByExamType(examType, startDate, endDate); }
}
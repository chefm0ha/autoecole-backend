package com.springBoot.autoEcole.service;

import com.springBoot.autoEcole.dto.CalendarExamDTO;
import com.springBoot.autoEcole.dto.ExamRequestDTO;
import com.springBoot.autoEcole.dto.ExamResponseDTO;
import com.springBoot.autoEcole.model.Exam;

import java.time.LocalDate;
import java.util.List;

public interface ExamService {
	Exam saveExam(Long applicationFileId, ExamRequestDTO examRequest);
	List<ExamResponseDTO> getExamsByApplicationFile(Long applicationFileId);
	Exam updateExamStatus(Long examId, String newStatus);

	// Calendar-specific methods
	List<CalendarExamDTO> getExamsByMonth(int year, int month);
	List<CalendarExamDTO> getExamsByDate(LocalDate date);
	List<CalendarExamDTO> getExamsByDateRange(LocalDate startDate, LocalDate endDate);
	List<CalendarExamDTO> getScheduledExamsFromDate(LocalDate fromDate);
}
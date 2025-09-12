package com.autoecole.service;

import com.autoecole.dto.response.CalendarExamDTO;
import com.autoecole.dto.request.ExamRequestDTO;
import com.autoecole.dto.response.ExamResponseDTO;
import com.autoecole.model.Exam;

import java.time.LocalDate;
import java.util.List;

public interface ExamService {
	Exam saveExam(Long applicationFileId, ExamRequestDTO examRequest);
	List<ExamResponseDTO> getExamsByApplicationFile(Long applicationFileId);
	Exam updateExamStatus(Long examId, String newStatus);

	// Calendar-specific methods
	List<CalendarExamDTO> getExamsByMonth(int year, int month);
	List<CalendarExamDTO> getExamsByDate(LocalDate date);
	List<Exam> getScheduledExamsThisWeek();
}
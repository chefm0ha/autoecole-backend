package com.springBoot.autoEcole.repository;

import com.springBoot.autoEcole.model.ApplicationFile;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.springBoot.autoEcole.model.Exam;

import java.time.LocalDate;
import java.util.List;

@Repository
@Transactional
public interface ExamDao extends CrudRepository<Exam, Long>, JpaSpecificationExecutor<Exam> {

	Long removeById(Long id);

	List<Exam> findByApplicationFileOrderByDateDesc(ApplicationFile applicationFile);

	@Modifying
	@Query(value = "CALL save_exam_with_logic(:p_application_file_id, :p_exam_type, :p_date, :p_status)",
			nativeQuery = true)
	void saveExamWithBusinessLogic(
			@Param("p_application_file_id") Long applicationFileId,
			@Param("p_exam_type") String examType,
			@Param("p_date") LocalDate date,
			@Param("p_status") String status
	);

	@Query(value = "SELECT * FROM exam WHERE application_file_id = :applicationFileId AND exam_type = :examType ORDER BY attempt_number DESC LIMIT 1",
			nativeQuery = true)
	Exam findLatestExamByApplicationFileAndType(
			@Param("applicationFileId") Long applicationFileId,
			@Param("examType") String examType
	);

	@Modifying
	@Query(value = "CALL update_exam_status(:p_exam_id, :p_new_status)", nativeQuery = true)
	void updateExamStatusWithBusinessLogic(
			@Param("p_exam_id") Long examId,
			@Param("p_new_status") String newStatus
	);

	// Calendar-specific queries
	@Query("SELECT e FROM Exam e " +
			"JOIN FETCH e.applicationFile af " +
			"JOIN FETCH af.candidate c " +
			"JOIN FETCH af.category cat " +
			"WHERE YEAR(e.date) = :year AND MONTH(e.date) = :month " +
			"ORDER BY e.date ASC, c.lastName ASC, c.firstName ASC")
	List<Exam> findExamsByYearAndMonth(
			@Param("year") int year,
			@Param("month") int month
	);

	@Query("SELECT e FROM Exam e " +
			"JOIN FETCH e.applicationFile af " +
			"JOIN FETCH af.candidate c " +
			"JOIN FETCH af.category cat " +
			"WHERE e.date = :date " +
			"ORDER BY e.examType ASC, c.lastName ASC, c.firstName ASC")
	List<Exam> findExamsByDate(@Param("date") LocalDate date);

	@Query("SELECT e FROM Exam e " +
			"JOIN FETCH e.applicationFile af " +
			"JOIN FETCH af.candidate c " +
			"JOIN FETCH af.category cat " +
			"WHERE e.date BETWEEN :startDate AND :endDate " +
			"ORDER BY e.date ASC, e.examType ASC, c.lastName ASC, c.firstName ASC")
	List<Exam> findExamsByDateRange(
			@Param("startDate") LocalDate startDate,
			@Param("endDate") LocalDate endDate
	);

	// Additional useful query for getting all scheduled exams
	@Query("SELECT e FROM Exam e " +
			"JOIN FETCH e.applicationFile af " +
			"JOIN FETCH af.candidate c " +
			"JOIN FETCH af.category cat " +
			"WHERE e.status = 'SCHEDULED' AND e.date >= :fromDate " +
			"ORDER BY e.date ASC, e.examType ASC, c.lastName ASC, c.firstName ASC")
	List<Exam> findScheduledExamsFromDate(@Param("fromDate") LocalDate fromDate);
}
package com.autoecole.repository;

import com.autoecole.model.ApplicationFile;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.autoecole.model.Exam;

import java.time.LocalDate;
import java.util.List;

@Repository
@Transactional
public interface ExamDao extends CrudRepository<Exam, Long>, JpaSpecificationExecutor<Exam> {

	Long removeById(Long id);

	List<Exam> findByApplicationFileOrderByDateDesc(ApplicationFile applicationFile);

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
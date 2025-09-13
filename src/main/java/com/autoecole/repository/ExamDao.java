package com.autoecole.repository;

import com.autoecole.enums.ExamStatus;
import com.autoecole.enums.ExamType;
import com.autoecole.model.ApplicationFile;
import org.springframework.data.domain.Pageable;
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

	Long countScheduledExamsByDateBetween(LocalDate startDate, LocalDate endDate);

	@Query("SELECT e FROM Exam e WHERE e.date > :date ORDER BY e.date ASC")
	List<Exam> findByDateAfterOrderByDateAsc(@Param("date") LocalDate date, Pageable pageable);

	@Query("SELECT COUNT(e) FROM Exam e WHERE e.examType = :examType AND e.date BETWEEN :startDate AND :endDate")
	int countAllByExamTypeAndDateBetween(
			@Param("examType") ExamType examType,
			@Param("startDate") LocalDate startDate,
			@Param("endDate") LocalDate endDate
	);

	@Query("SELECT COUNT(e) FROM Exam e WHERE e.examType = :examType AND e.status = :status AND e.date BETWEEN :startDate AND :endDate")
	int countByExamTypeAndStatusAndDateBetween(
			@Param("examType") ExamType examType,
			@Param("status") ExamStatus status,
			@Param("startDate") LocalDate startDate,
			@Param("endDate") LocalDate endDate
	);
}
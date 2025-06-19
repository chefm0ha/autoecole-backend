package com.springBoot.autoEcole.repository;

import java.time.LocalDate;
import java.util.Collection;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.springBoot.autoEcole.model.Candidate;
import com.springBoot.autoEcole.model.Exam;

@Repository
@Transactional
public interface IExamDao extends CrudRepository<Exam, Long>, JpaSpecificationExecutor<Exam> {

	Long removeById(Long id);

	@Query("SELECT e FROM Exam e JOIN FETCH e.candidate WHERE e.date BETWEEN :today AND :compareDate")
	Collection<Exam> findExamOnPeriod(@Param("today") LocalDate today, @Param("compareDate") LocalDate compareDate);

	@Query("SELECT COUNT(e) FROM Exam e WHERE e.date BETWEEN :today AND :weekDate AND e.examType = :examType")
	Integer getCountExamOnWeekByType(@Param("examType") String examType, @Param("today") LocalDate today, @Param("weekDate") LocalDate weekDate);

	long countByCandidateAndExamType(Candidate candidate, String examType);

	@Query("SELECT COUNT(e) FROM Exam e WHERE e.examType = 'Code' AND e.attemptNumber = 1 AND e.status = 'PASSED'")
	Float getCountExamCodeValid();

	@Query("SELECT COUNT(e) FROM Exam e WHERE e.examType = 'Conduite' AND e.attemptNumber = 1 AND e.status = 'PASSED'")
	Float getCountExamConduiteValid();

	@Query("SELECT COUNT(e) FROM Exam e WHERE e.examType = :examType AND e.attemptNumber = 1")
	Float getCountByExamType(@Param("examType") String examType);
}
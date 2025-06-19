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
public interface ExamDao extends CrudRepository<Exam, Long>, JpaSpecificationExecutor<Exam> {

	/**
	 * Deletes an exam by its identifier
	 * @param id - The exam ID to delete
	 * @return The number of deleted records (1 if successful, 0 if not found)
	 */
	Long removeById(Long id);

	/**
	 * Retrieves all exams scheduled within a given period
	 * Uses JOIN FETCH to eagerly load candidate information (avoids N+1 problem)
	 * @param today - Start date of the period
	 * @param compareDate - End date of the period
	 * @return Collection of exams within the specified period
	 */
	@Query("SELECT e FROM Exam e JOIN FETCH e.candidate WHERE e.date BETWEEN :today AND :compareDate")
	Collection<Exam> findExamOnPeriod(@Param("today") LocalDate today, @Param("compareDate") LocalDate compareDate);

	/**
	 * Counts the number of exams of a specific type within a week
	 * Useful for scheduling and capacity management
	 * @param examType - Type of exam ("Code" or "Driving")
	 * @param today - Start date of the week
	 * @param weekDate - End date of the week
	 * @return Number of exams of the specified type within the period
	 */
	@Query("SELECT COUNT(e) FROM Exam e WHERE e.date BETWEEN :today AND :weekDate AND e.examType = :examType")
	Integer getCountExamOnWeekByType(@Param("examType") String examType, @Param("today") LocalDate today, @Param("weekDate") LocalDate weekDate);

	/**
	 * Counts the number of exams a specific candidate has taken for a given exam type
	 * Helps determine if it's the candidate's first attempt for this exam type
	 * @param candidate - The candidate in question
	 * @param examType - Type of exam ("Code" or "Driving")
	 * @return Number of exams taken by the candidate for this type
	 */
	long countByCandidateAndExamType(Candidate candidate, String examType);

	/**
	 * Counts the number of theory exams passed on the first attempt
	 * Used to calculate the success rate for the theoretical exam
	 * @return Number of theory exams passed on the first attempt
	 */
	@Query("SELECT COUNT(e) FROM Exam e WHERE e.examType = 'Code' AND e.attemptNumber = 1 AND e.status = 'PASSED'")
	Float getCountExamCodeValid();

	/**
	 * Counts the number of driving exams passed on the first attempt
	 * Used to calculate the success rate for the practical exam
	 * @return Number of driving exams passed on the first attempt
	 */
	@Query("SELECT COUNT(e) FROM Exam e WHERE e.examType = 'Driving' AND e.attemptNumber = 1 AND e.status = 'PASSED'")
	Float getCountExamDrivingValid();

	/**
	 * Counts the total number of first attempts for a specific exam type
	 * Used as denominator when calculating success rates
	 * @param examType - Type of exam ("Code" or "Driving")
	 * @return Total number of first attempts for this exam type
	 */
	@Query("SELECT COUNT(e) FROM Exam e WHERE e.examType = :examType AND e.attemptNumber = 1")
	Float getCountByExamType(@Param("examType") String examType);
}
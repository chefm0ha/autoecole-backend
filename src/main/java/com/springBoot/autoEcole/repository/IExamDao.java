package com.springBoot.autoEcole.repository;

import java.util.Collection;
import java.util.Date;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.springBoot.autoEcole.model.Candidat;
import com.springBoot.autoEcole.model.Exam;

@Repository
@Transactional
public interface IExamDao extends CrudRepository<Exam, Long>, JpaSpecificationExecutor<Exam> {
	public Long removeById(Long id);
	
	@Query("select e from Exam e join fetch e.candidat"
			+ " where e.dateExam between :today and :compareDate")
	public Collection<Exam> findExamOnPeriod(@Param("today") Date today, @Param("compareDate") Date compareDate);

	@Query("select count(e) from Exam e"
			+ " where e.dateExam between :today and :weekDate and e.typeExam = :typeExam")
	public Integer getCountExamOnWeekByType(@Param("typeExam")String typeExam,@Param("today") Date today, @Param("weekDate") Date weekDate );
	
	public long countByCandidatAndTypeExam(Candidat candidat, String typeExam);
	
	@Query("select count(e) from Exam e "
			+ "where e.typeExam = 'Code' and e.firstExam = true and e.result>=30")
	public Float getCountExamCodeValid();
	
	@Query("select count(e) from Exam e "
			+ "where e.typeExam = 'Conduite' and e.firstExam = true and e.result ='Admis'")
	public Float getCountExamConduiteValid();
	
	@Query("select count(e) from Exam e"
			+ " where e.typeExam = :typeExam and e.firstExam = true ")
	public Float getcountByTypeExam(@Param("typeExam")String typeExam);
}

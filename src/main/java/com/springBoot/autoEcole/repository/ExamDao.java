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
	Long removeById(Long id);
}
package com.springBoot.autoEcole.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.springBoot.autoEcole.model.Exam;

@Repository
@Transactional
public interface ExamDao extends CrudRepository<Exam, Long>, JpaSpecificationExecutor<Exam> {
	Long removeById(Long id);
}
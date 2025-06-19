package com.springBoot.autoEcole.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.springBoot.autoEcole.model.Insurance;


@Repository
@Transactional
public interface IAssuranceDao extends CrudRepository<Insurance, Long> {

	Long removeById(Long id);

}

package com.springBoot.autoEcole.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.springBoot.autoEcole.model.Instructor;

@Repository
@Transactional
public interface IInstructorDao extends CrudRepository<Instructor, String> {

    Instructor findByCin(String cin);

    Long removeByCin(String cin);
}
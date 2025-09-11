package com.autoecole.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.autoecole.model.Instructor;

@Repository
@Transactional
public interface InstructorDao extends CrudRepository<Instructor, String> {

    Instructor findByCin(String cin);

    Long removeByCin(String cin);
}
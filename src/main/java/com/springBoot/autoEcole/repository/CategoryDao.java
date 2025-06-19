package com.springBoot.autoEcole.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.springBoot.autoEcole.model.Category;

@Repository
@Transactional
public interface CategoryDao extends CrudRepository<Category, String> {

    Category findByCode(String code);
}
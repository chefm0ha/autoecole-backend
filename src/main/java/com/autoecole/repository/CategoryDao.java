package com.autoecole.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.autoecole.model.Category;

@Repository
@Transactional
public interface CategoryDao extends CrudRepository<Category, String> {
    Category findByCode(String code);
}
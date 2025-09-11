package com.autoecole.service.impl;

import java.util.Collection;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.autoecole.model.Category;
import com.autoecole.repository.CategoryDao;
import com.autoecole.service.CategoryService;

@Service
@Transactional
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryDao categoryDao;

    @Override
    public Collection<Category> findAllCategory() {
        return (Collection<Category>) categoryDao.findAll();
    }

    @Override
    public Category findByCode(String code) {
        return categoryDao.findByCode(code);
    }
}
package com.springBoot.autoEcole.service.impl;

import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.springBoot.autoEcole.model.Category;
import com.springBoot.autoEcole.repository.CategoryDao;
import com.springBoot.autoEcole.service.CategoryService;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryDao categoryDao;

    @Override
    public Collection<Category> findAllCategory() {
        return (Collection<Category>) categoryDao.findAll();
    }

    @Override
    public Category saveCategory(Category category) {
        return categoryDao.save(category);
    }

    @Override
    public Category findByCode(String code) {
        return categoryDao.findByCode(code);
    }
}
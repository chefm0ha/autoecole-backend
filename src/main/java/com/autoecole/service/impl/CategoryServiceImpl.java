package com.autoecole.service.impl;

import java.util.Collection;

import com.autoecole.exception.BusinessException;
import com.autoecole.exception.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.autoecole.model.Category;
import com.autoecole.repository.CategoryDao;
import com.autoecole.service.CategoryService;

@Slf4j
@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryDao categoryDao;

    @Override
    public Collection<Category> findAllCategory() {
        try {
            return (Collection<Category>) categoryDao.findAll();
        } catch (DataAccessException e) {
            log.error("Database error while retrieving categories", e);
            throw new BusinessException("Database error occurred while retrieving categories");
        } catch (Exception e) {
            log.error("Unexpected error while retrieving categories", e);
            throw new BusinessException("An unexpected error occurred while retrieving categories");
        }
    }

    @Override
    public Category findByCode(String code) {
        try {
            if (code == null || code.trim().isEmpty()) {
                throw new IllegalArgumentException("Category code is required");
            }

            Category category = categoryDao.findByCode(code);
            if (category == null) {
                throw new NotFoundException("Category not found with code: " + code);
            }

            return category;
        } catch (NotFoundException | IllegalArgumentException e) {
            throw e;
        } catch (DataAccessException e) {
            log.error("Database error while finding category with code: {}", code, e);
            throw new BusinessException("Database error occurred while searching for category");
        } catch (Exception e) {
            log.error("Unexpected error while finding category with code: {}", code, e);
            throw new BusinessException("An unexpected error occurred while searching for category");
        }
    }
}
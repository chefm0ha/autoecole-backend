package com.springBoot.autoEcole.service;

import java.util.Collection;
import com.springBoot.autoEcole.model.Category;

public interface CategoryService {
    Collection<Category> findAllCategory();
    Category saveCategory(Category category);
    Category findByCode(String code);
}

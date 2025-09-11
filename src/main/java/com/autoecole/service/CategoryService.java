package com.autoecole.service;

import java.util.Collection;
import com.autoecole.model.Category;

public interface CategoryService {
    Collection<Category> findAllCategory();
    Category findByCode(String code);
}

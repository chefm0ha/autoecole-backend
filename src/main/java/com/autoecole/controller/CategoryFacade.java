package com.autoecole.controller;

import java.util.Collection;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.autoecole.model.Category;
import com.autoecole.service.CategoryService;

@RestController
@RequestMapping("/category")
@CrossOrigin
@AllArgsConstructor
public class CategoryFacade {

    private final CategoryService categoryService;

    @GetMapping("/getAllCategories")
    public Collection<Category> getAllCategories() {
        return categoryService.findAllCategory();
    }
}
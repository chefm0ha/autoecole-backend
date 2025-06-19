package com.springBoot.autoEcole.web.rest;

import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.springBoot.autoEcole.model.Category;
import com.springBoot.autoEcole.service.CategoryService;

@RestController
@RequestMapping("/category")
@CrossOrigin
public class CategoryFacade {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/getCategories")
    public Collection<Category> getCategories() {
        return categoryService.findAllCategory();
    }

    @PostMapping("/saveCategory")
    public Category saveCategory(@RequestBody Category category) {
        return categoryService.saveCategory(category);
    }

    @GetMapping("/getCategory/{code}")
    public Category getCategoryByCode(@PathVariable String code) {
        return categoryService.findByCode(code);
    }
}
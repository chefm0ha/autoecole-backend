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

    @GetMapping("/getAllCategories")
    public Collection<Category> getAllCategories() {
        return categoryService.findAllCategory();
    }
}
package com.goncalo.grocery_api.service;

import com.goncalo.grocery_api.model.Category;
import com.goncalo.grocery_api.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category createCategory(String name) {
        Category category = new Category(null, name);
        return categoryRepository.save(category);
    }

    
}
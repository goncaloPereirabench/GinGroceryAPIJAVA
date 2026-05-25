package com.goncalo.grocery_api.controller;

import com.goncalo.grocery_api.dto.CategoryRequestDTO;
import com.goncalo.grocery_api.dto.CategoryResponseDTO;
import com.goncalo.grocery_api.model.Category;
import com.goncalo.grocery_api.service.CategoryService;
import com.goncalo.grocery_api.mapper.CategoryMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getCategories() {
        List<CategoryResponseDTO> response = categoryService.getAllCategories()
                .stream()
                .map(CategoryMapper::toResponseDTO)
                .toList();

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<CategoryResponseDTO> createCategory(
            @Valid @RequestBody CategoryRequestDTO request
    ) {
        Category category = categoryService.createCategory(request.getName());

        CategoryResponseDTO response = CategoryMapper.toResponseDTO(category);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
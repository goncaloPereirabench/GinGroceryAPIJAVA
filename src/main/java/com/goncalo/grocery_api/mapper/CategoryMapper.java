package com.goncalo.grocery_api.mapper;

import com.goncalo.grocery_api.dto.CategoryResponseDTO;
import com.goncalo.grocery_api.model.Category;

public class CategoryMapper {

    public static CategoryResponseDTO toResponseDTO(Category category) {
        return new CategoryResponseDTO(
                category.getId(),
                category.getName()
        );
    }
}
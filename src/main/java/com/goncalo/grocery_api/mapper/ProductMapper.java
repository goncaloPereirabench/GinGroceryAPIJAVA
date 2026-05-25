package com.goncalo.grocery_api.mapper;

import com.goncalo.grocery_api.dto.ProductResponseDTO;
import com.goncalo.grocery_api.model.Product;

public class ProductMapper {

    public static ProductResponseDTO toResponseDTO(Product product) {
        return new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getCategory().getId(),
                product.getCategory().getName()
        );
    }
}
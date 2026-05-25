package com.goncalo.grocery_api.controller;

import com.goncalo.grocery_api.dto.ProductResponseDTO;
import com.goncalo.grocery_api.model.Category;
import com.goncalo.grocery_api.model.Product;
import com.goncalo.grocery_api.service.CategoryService;
import com.goncalo.grocery_api.service.ProductService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class GroceryGraphQlController {

    private final CategoryService categoryService;
    private final ProductService productService;

    public GroceryGraphQlController(CategoryService categoryService, ProductService productService) {
        this.categoryService = categoryService;
        this.productService = productService;
    }

    @QueryMapping
    public List<CategoryPayload> categories() {
        return categoryService.getAllCategories()
                .stream()
                .map(GroceryGraphQlController::toCategoryPayload)
                .toList();
    }

    @QueryMapping
    public List<ProductPayload> products() {
        return productService.getAllProductResponses()
                .stream()
                .map(GroceryGraphQlController::toProductPayload)
                .toList();
    }

    @QueryMapping
    public ProductPayload productById(@Argument Long id) {
        return toProductPayload(productService.getProductResponseById(id));
    }

    @MutationMapping
    public CategoryPayload createCategory(@Argument CategoryInput input) {
        Category category = categoryService.createCategory(input.name());

        return toCategoryPayload(category);
    }

    @MutationMapping
    public ProductPayload createProduct(@Argument ProductInput input) {
        Product product = productService.createProduct(
                input.name(),
                input.price(),
                input.categoryId()
        );

        return toProductPayload(product);
    }

    private static CategoryPayload toCategoryPayload(Category category) {
        return new CategoryPayload(category.getId(), category.getName());
    }

    private static ProductPayload toProductPayload(Product product) {
        return new ProductPayload(
                product.getId(),
                product.getName(),
                product.getPrice(),
                toCategoryPayload(product.getCategory())
        );
    }

    private static ProductPayload toProductPayload(ProductResponseDTO product) {
        return new ProductPayload(
                product.getId(),
                product.getName(),
                product.getPrice(),
                new CategoryPayload(product.getCategoryId(), product.getCategoryName())
        );
    }

    public record CategoryInput(String name) {
    }

    public record ProductInput(String name, Double price, Long categoryId) {
    }

    public record CategoryPayload(Long id, String name) {
    }

    public record ProductPayload(Long id, String name, Double price, CategoryPayload category) {
    }
}

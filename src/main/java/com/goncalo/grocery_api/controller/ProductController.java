package com.goncalo.grocery_api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.goncalo.grocery_api.dto.ProductRequestDTO;
import com.goncalo.grocery_api.dto.ProductResponseDTO;
import com.goncalo.grocery_api.model.Product;
import com.goncalo.grocery_api.service.ProductService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RequestParam;

import com.goncalo.grocery_api.mapper.ProductMapper;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getProducts() {
        return ResponseEntity.ok(productService.getAllProductResponses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductResponseById(id));
    }

    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(
            @Valid @RequestBody ProductRequestDTO request
    ) {

        Product createdProduct = productService.createProduct(
                request.getName(),
                request.getPrice(),
                request.getCategoryId()
        );

        ProductResponseDTO response = ProductMapper.toResponseDTO(createdProduct);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequestDTO request
    ) {

        Product updatedProduct = productService.updateProduct(
                id,
                request.getName(),
                request.getPrice(),
                request.getCategoryId()
        );

        if (updatedProduct == null) {
            return ResponseEntity.notFound().build();
        }

        ProductResponseDTO response = ProductMapper.toResponseDTO(updatedProduct);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteProduct(@PathVariable Long id) {
        boolean deleted = productService.deleteProduct(id);

        if (!deleted) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponseDTO>> searchProducts(
            @RequestParam String name
    ) {
        return ResponseEntity.ok(productService.searchProductResponsesByName(name));
    }

    @GetMapping("/paginated")
    public ResponseEntity<List<ProductResponseDTO>> getProductsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        return ResponseEntity.ok(
                productService.getPaginatedProductResponses(page, size, sortBy)
        );
    }

    @GetMapping("/filter")
    public ResponseEntity<List<ProductResponseDTO>> getProductsByMinPrice(
            @RequestParam Double minPrice
    ) {
        return ResponseEntity.ok(productService.getProductsByMinPrice(minPrice));
    }

    @GetMapping("/advanced-search")
    public ResponseEntity<List<ProductResponseDTO>> advancedSearch(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice
    ) {
        return ResponseEntity.ok(
                productService.searchProducts(name, minPrice, maxPrice)
        );
    }

    @GetMapping("/native/by-category")
    public ResponseEntity<List<ProductResponseDTO>> getProductsByCategoryNameNative(
            @RequestParam String categoryName
    ) {
        return ResponseEntity.ok(
                productService.getProductsByCategoryNameNative(categoryName)
        );
    }
}

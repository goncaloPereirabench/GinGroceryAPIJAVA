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
import org.springframework.web.bind.annotation.RestController;

import com.goncalo.grocery_api.dto.ProductRequestDTO;
import com.goncalo.grocery_api.dto.ProductResponseDTO;
import com.goncalo.grocery_api.model.Product;
import com.goncalo.grocery_api.service.ProductService;

@RestController
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products")
    public List<ProductResponseDTO> getProducts() {
        return productService.getAllProducts()
            .stream()
            .map(product -> new ProductResponseDTO(
                    product.getId(),
                    product.getName(),
                    product.getPrice()
            ))
            .toList();
    }
    
@GetMapping("/products/{id}")
   public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Long id) {

    Product product = productService.getProductById(id);

    ProductResponseDTO response = new ProductResponseDTO(
            product.getId(),
            product.getName(),
            product.getPrice()
    );

    return ResponseEntity.ok(response);
}

     @PostMapping("/products")
   public ResponseEntity<ProductResponseDTO> createProduct(
        @RequestBody ProductRequestDTO request
) {

    Product createdProduct = productService.createProduct(
            request.getName(),
            request.getPrice()
    );

    ProductResponseDTO response = new ProductResponseDTO(
            createdProduct.getId(),
            createdProduct.getName(),
            createdProduct.getPrice()
    );

    return ResponseEntity.status(HttpStatus.CREATED).body(response); 
}

@PutMapping("/products/{id}")
public ResponseEntity<ProductResponseDTO> updateProduct(
        @PathVariable Long id,
        @RequestBody ProductRequestDTO request
) {

    Product updatedProduct = productService.updateProduct(
            id,
            new Product(null, request.getName(), request.getPrice())
    );

    if (updatedProduct == null) {
        return ResponseEntity.notFound().build();
    }

    ProductResponseDTO response = new ProductResponseDTO(
            updatedProduct.getId(),
            updatedProduct.getName(),
            updatedProduct.getPrice()
    );

    return ResponseEntity.ok(response);
}

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Boolean> deleteProduct(@PathVariable Long id) {
         boolean deleted = productService.deleteProduct(id);

    if (!deleted) {
        return ResponseEntity.notFound().build();
    }

    return ResponseEntity.noContent().build();
    }
}
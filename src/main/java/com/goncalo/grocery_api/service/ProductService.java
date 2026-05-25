package com.goncalo.grocery_api.service;

import com.goncalo.grocery_api.exception.ProductNotFoundException;
import com.goncalo.grocery_api.model.Product;
import com.goncalo.grocery_api.model.Category;
import com.goncalo.grocery_api.repository.ProductRepository;
import com.goncalo.grocery_api.repository.CategoryRepository;

import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

import com.goncalo.grocery_api.exception.CategoryNotFoundException;
import com.goncalo.grocery_api.dto.ProductResponseDTO;
import com.goncalo.grocery_api.mapper.ProductMapper;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;

    }

    public Page<Product> getProductsPaginated(
            int page,
            int size,
            String sortBy
    ) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(sortBy)
        );

        return productRepository.findAll(pageable);
    }

    public List<Product> searchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Transactional
    public Product createProduct(String name, Double price, Long categoryId) {
        Category category = getCategoryOrThrow(categoryId);

        Product product = new Product(null, name, price, category);

        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(Long id, String name, Double price, Long categoryId) {
        Product existingProduct = getProductById(id);
        Category category = getCategoryOrThrow(categoryId);

        existingProduct.setName(name);
        existingProduct.setPrice(price);
        existingProduct.setCategory(category);

        return productRepository.save(existingProduct);
    }

    @Transactional
    public boolean deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            return false;
        }

        productRepository.deleteById(id);
        return true;
    }

    private Category getCategoryOrThrow(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getAllProductResponses() {
        return productRepository.findAllWithCategoryEntityGraph()
                .stream()
                .map(ProductMapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductResponseDTO getProductResponseById(Long id) {
        Product product = getProductById(id);

        return ProductMapper.toResponseDTO(product);
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDTO> searchProductResponsesByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(ProductMapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getPaginatedProductResponses(
            int page,
            int size,
            String sortBy
    ) {
        return getProductsPaginated(page, size, sortBy)
                .stream()
                .map(ProductMapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getProductsByMinPrice(Double minPrice) {
        return productRepository.findProductsWithCategoryByMinPrice(minPrice)
                .stream()
                .map(ProductMapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDTO> searchProducts(
            String name,
            Double minPrice,
            Double maxPrice
    ) {
        return productRepository.searchProducts(name, minPrice, maxPrice)
                .stream()
                .map(ProductMapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getAllProductResponsesProjection() {
        return productRepository.findAllProductResponses();
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getProductsByCategoryNameNative(String categoryName) {
        return productRepository.findProductsByCategoryNameNative(categoryName)
                .stream()
                .map(ProductMapper::toResponseDTO)
                .toList();
    }
}

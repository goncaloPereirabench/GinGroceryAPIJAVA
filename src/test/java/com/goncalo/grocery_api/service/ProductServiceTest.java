package com.goncalo.grocery_api.service;

import com.goncalo.grocery_api.dto.ProductResponseDTO;
import com.goncalo.grocery_api.exception.CategoryNotFoundException;
import com.goncalo.grocery_api.exception.ProductNotFoundException;
import com.goncalo.grocery_api.model.Category;
import com.goncalo.grocery_api.model.Product;
import com.goncalo.grocery_api.repository.CategoryRepository;
import com.goncalo.grocery_api.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void getProductByIdReturnsProductWhenFound() {
        Category category = new Category(1L, "Fruit");
        Product product = new Product(10L, "Apple", 1.25, category);
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));

        Product result = productService.getProductById(10L);

        assertThat(result).isSameAs(product);
    }

    @Test
    void getProductByIdThrowsWhenMissing() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductById(99L))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessage("Product with id 99 not found");
    }

    @Test
    void createProductUsesCategoryAndSavesProduct() {
        Category category = new Category(2L, "Bakery");
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product product = invocation.getArgument(0);
            product.setId(15L);
            return product;
        });

        Product created = productService.createProduct("Bread", 2.50, 2L);

        assertThat(created.getId()).isEqualTo(15L);
        assertThat(created.getName()).isEqualTo("Bread");
        assertThat(created.getPrice()).isEqualTo(2.50);
        assertThat(created.getCategory()).isSameAs(category);
    }

    @Test
    void createProductThrowsWhenCategoryIsMissing() {
        when(categoryRepository.findById(7L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.createProduct("Milk", 1.10, 7L))
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessage("Category with id 7 not found");

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void updateProductChangesFieldsAndSaves() {
        Category oldCategory = new Category(1L, "Fruit");
        Category newCategory = new Category(3L, "Dairy");
        Product existingProduct = new Product(20L, "Yogurt", 1.00, oldCategory);
        when(productRepository.findById(20L)).thenReturn(Optional.of(existingProduct));
        when(categoryRepository.findById(3L)).thenReturn(Optional.of(newCategory));
        when(productRepository.save(existingProduct)).thenReturn(existingProduct);

        Product updated = productService.updateProduct(20L, "Greek Yogurt", 1.80, 3L);

        assertThat(updated.getName()).isEqualTo("Greek Yogurt");
        assertThat(updated.getPrice()).isEqualTo(1.80);
        assertThat(updated.getCategory()).isSameAs(newCategory);
        verify(productRepository).save(existingProduct);
    }

    @Test
    void deleteProductReturnsFalseWhenProductDoesNotExist() {
        when(productRepository.existsById(50L)).thenReturn(false);

        boolean deleted = productService.deleteProduct(50L);

        assertThat(deleted).isFalse();
        verify(productRepository, never()).deleteById(50L);
    }

    @Test
    void deleteProductDeletesAndReturnsTrueWhenProductExists() {
        when(productRepository.existsById(50L)).thenReturn(true);

        boolean deleted = productService.deleteProduct(50L);

        assertThat(deleted).isTrue();
        verify(productRepository).deleteById(50L);
    }

    @Test
    void getProductsPaginatedBuildsPageRequestWithSort() {
        Page<Product> page = new PageImpl<>(List.of());
        when(productRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<Product> result = productService.getProductsPaginated(2, 10, "name");

        assertThat(result).isSameAs(page);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(productRepository).findAll(pageableCaptor.capture());

        Pageable pageable = pageableCaptor.getValue();
        assertThat(pageable.getPageNumber()).isEqualTo(2);
        assertThat(pageable.getPageSize()).isEqualTo(10);
        assertThat(pageable.getSort().getOrderFor("name")).isNotNull();
    }

    @Test
    void searchProductsMapsProductsToResponseDtos() {
        Category category = new Category(4L, "Produce");
        Product product = new Product(30L, "Tomato", 0.80, category);
        when(productRepository.searchProducts("tom", 0.50, 2.00)).thenReturn(List.of(product));

        List<ProductResponseDTO> results = productService.searchProducts("tom", 0.50, 2.00);

        assertThat(results).hasSize(1);
        ProductResponseDTO dto = results.getFirst();
        assertThat(dto.getId()).isEqualTo(30L);
        assertThat(dto.getName()).isEqualTo("Tomato");
        assertThat(dto.getPrice()).isEqualTo(0.80);
        assertThat(dto.getCategoryId()).isEqualTo(4L);
        assertThat(dto.getCategoryName()).isEqualTo("Produce");
    }
}

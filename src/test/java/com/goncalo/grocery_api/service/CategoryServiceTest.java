package com.goncalo.grocery_api.service;

import com.goncalo.grocery_api.model.Category;
import com.goncalo.grocery_api.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void getAllCategoriesReturnsRepositoryResults() {
        List<Category> categories = List.of(
                new Category(1L, "Fruit"),
                new Category(2L, "Bakery")
        );
        when(categoryRepository.findAll()).thenReturn(categories);

        List<Category> result = categoryService.getAllCategories();

        assertThat(result).isSameAs(categories);
    }

    @Test
    void createCategorySavesCategoryWithName() {
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
            Category category = invocation.getArgument(0);
            category.setId(10L);
            return category;
        });

        Category created = categoryService.createCategory("Drinks");

        assertThat(created.getId()).isEqualTo(10L);
        assertThat(created.getName()).isEqualTo("Drinks");
        verify(categoryRepository).save(any(Category.class));
    }
}

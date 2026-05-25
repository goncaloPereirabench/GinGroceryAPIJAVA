package com.goncalo.grocery_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goncalo.grocery_api.dto.ProductRequestDTO;
import com.goncalo.grocery_api.dto.ProductResponseDTO;
import com.goncalo.grocery_api.exception.GlobalExceptionHandler;
import com.goncalo.grocery_api.exception.ProductNotFoundException;
import com.goncalo.grocery_api.model.Category;
import com.goncalo.grocery_api.model.Product;
import com.goncalo.grocery_api.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(new ProductController(productService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getProductsReturnsProducts() throws Exception {
        ProductResponseDTO product = new ProductResponseDTO(1L, "Apple", 1.25, 2L, "Fruit");
        when(productService.getAllProductResponses()).thenReturn(List.of(product));

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Apple"))
                .andExpect(jsonPath("$[0].price").value(1.25))
                .andExpect(jsonPath("$[0].categoryId").value(2))
                .andExpect(jsonPath("$[0].categoryName").value("Fruit"));
    }

    @Test
    void getProductByIdReturnsNotFoundWhenServiceThrows() throws Exception {
        when(productService.getProductResponseById(99L)).thenThrow(new ProductNotFoundException(99L));

        mockMvc.perform(get("/products/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Product with id 99 not found"));
    }

    @Test
    void createProductReturnsCreatedProduct() throws Exception {
        ProductRequestDTO request = new ProductRequestDTO();
        request.setName("Bread");
        request.setPrice(2.50);
        request.setCategoryId(3L);

        Category category = new Category(3L, "Bakery");
        Product product = new Product(11L, "Bread", 2.50, category);
        when(productService.createProduct("Bread", 2.50, 3L)).thenReturn(product);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(11))
                .andExpect(jsonPath("$.name").value("Bread"))
                .andExpect(jsonPath("$.price").value(2.50))
                .andExpect(jsonPath("$.categoryId").value(3))
                .andExpect(jsonPath("$.categoryName").value("Bakery"));
    }

    @Test
    void createProductReturnsBadRequestForInvalidBody() throws Exception {
        ProductRequestDTO request = new ProductRequestDTO();
        request.setName("");
        request.setPrice(-1.00);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.fieldErrors.name").value("Product name is required"))
                .andExpect(jsonPath("$.fieldErrors.price").value("Product price must be positive"))
                .andExpect(jsonPath("$.fieldErrors.categoryId").value("Category id is required"));

        verifyNoInteractions(productService);
    }

    @Test
    void deleteProductReturnsNoContentWhenDeleted() throws Exception {
        when(productService.deleteProduct(5L)).thenReturn(true);

        mockMvc.perform(delete("/products/5"))
                .andExpect(status().isNoContent());

        verify(productService).deleteProduct(5L);
    }

    @Test
    void deleteProductReturnsNotFoundWhenMissing() throws Exception {
        when(productService.deleteProduct(5L)).thenReturn(false);

        mockMvc.perform(delete("/products/5"))
                .andExpect(status().isNotFound());
    }
}

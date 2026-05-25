package com.goncalo.grocery_api.component;

import com.goncalo.grocery_api.model.Category;
import com.goncalo.grocery_api.model.Product;
import com.goncalo.grocery_api.repository.CategoryRepository;
import com.goncalo.grocery_api.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.ExecutionGraphQlService;
import org.springframework.graphql.test.tester.ExecutionGraphQlServiceTester;
import org.springframework.graphql.test.tester.GraphQlTester;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class GroceryGraphQlComponentTest {

    @Autowired
    private ExecutionGraphQlService graphQlService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    private GraphQlTester graphQlTester;

    @BeforeEach
    void cleanDatabase() {
        graphQlTester = ExecutionGraphQlServiceTester.create(graphQlService);
        productRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    void createCategoryMutationPersistsCategory() {
        graphQlTester.document("""
                        mutation($input: CategoryInput!) {
                          createCategory(input: $input) {
                            id
                            name
                          }
                        }
                        """)
                .variable("input", Map.of("name", "Frozen"))
                .execute()
                .path("createCategory.name")
                .entity(String.class)
                .isEqualTo("Frozen");

        assertThat(categoryRepository.findAll())
                .singleElement()
                .extracting(Category::getName)
                .isEqualTo("Frozen");
    }

    @Test
    void productsQueryReturnsNestedCategoryData() {
        Category category = categoryRepository.save(new Category(null, "Fruit"));
        productRepository.save(new Product(null, "Apple", 1.25, category));

        graphQlTester.document("""
                        query {
                          products {
                            name
                            price
                            category {
                              name
                            }
                          }
                        }
                        """)
                .execute()
                .path("products[0].name")
                .entity(String.class)
                .isEqualTo("Apple")
                .path("products[0].price")
                .entity(Double.class)
                .isEqualTo(1.25)
                .path("products[0].category.name")
                .entity(String.class)
                .isEqualTo("Fruit");
    }

    @Test
    void createProductMutationUsesExistingCategory() {
        Category category = categoryRepository.save(new Category(null, "Bakery"));

        graphQlTester.document("""
                        mutation($input: ProductInput!) {
                          createProduct(input: $input) {
                            name
                            price
                            category {
                              id
                              name
                            }
                          }
                        }
                        """)
                .variable("input", Map.of(
                        "name", "Bread",
                        "price", 2.5,
                        "categoryId", category.getId().toString()
                ))
                .execute()
                .path("createProduct.name")
                .entity(String.class)
                .isEqualTo("Bread")
                .path("createProduct.price")
                .entity(Double.class)
                .isEqualTo(2.5)
                .path("createProduct.category.name")
                .entity(String.class)
                .isEqualTo("Bakery");

        assertThat(productRepository.findAll())
                .singleElement()
                .extracting(Product::getName)
                .isEqualTo("Bread");
    }
}

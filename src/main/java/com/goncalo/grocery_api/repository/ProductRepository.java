package com.goncalo.grocery_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;

import com.goncalo.grocery_api.model.Product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.goncalo.grocery_api.dto.ProductResponseDTO;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByNameContainingIgnoreCase(String name);

    @EntityGraph(attributePaths = "category")
    @Query("SELECT p FROM Product p")
    List<Product> findAllWithCategoryEntityGraph();

    @Query("""
    SELECT p
    FROM Product p
    JOIN FETCH p.category
    WHERE p.price >= :minPrice
""")
    List<Product> findProductsWithCategoryByMinPrice(
            @Param("minPrice") Double minPrice
    );

    @Query("""
    SELECT p
    FROM Product p
    JOIN FETCH p.category
    WHERE (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))
      AND (:minPrice IS NULL OR p.price >= :minPrice)
      AND (:maxPrice IS NULL OR p.price <= :maxPrice)
""")
    List<Product> searchProducts(
            @Param("name") String name,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice
    );

    @Query("""
    SELECT new com.goncalo.grocery_api.dto.ProductResponseDTO(
        p.id,
        p.name,
        p.price,
        c.id,
        c.name
    )
    FROM Product p
    JOIN p.category c
""")
    List<ProductResponseDTO> findAllProductResponses();

    @Query(
            value = """
            SELECT p.*
            FROM product p
            JOIN category c ON p.category_id = c.id
            WHERE LOWER(c.name) = LOWER(:categoryName)
        """,
            nativeQuery = true
    )
    List<Product> findProductsByCategoryNameNative(
            @Param("categoryName") String categoryName
    );
}

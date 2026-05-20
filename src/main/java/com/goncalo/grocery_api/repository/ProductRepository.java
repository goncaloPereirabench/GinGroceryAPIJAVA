package com.goncalo.grocery_api.repository;

import com.goncalo.grocery_api.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
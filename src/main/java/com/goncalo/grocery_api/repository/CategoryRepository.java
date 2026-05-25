package com.goncalo.grocery_api.repository;

import com.goncalo.grocery_api.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    
}
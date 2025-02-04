package com.himedia.luckydokiapi.domain.product.repository;


import com.himedia.luckydokiapi.domain.product.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("select p from Category p")
    List<Category> findCategory();

}

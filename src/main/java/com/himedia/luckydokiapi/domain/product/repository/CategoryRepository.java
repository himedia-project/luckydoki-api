package com.himedia.luckydokiapi.domain.product.repository;


import com.himedia.luckydokiapi.domain.product.entity.Category;
import com.himedia.luckydokiapi.domain.product.repository.querydsl.CategoryRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long>, CategoryRepositoryCustom {
    @Query("select c from Category c where c.id=:id ")
    List<Category> findByCategoryId(@Param("id") Long categoryId);
}

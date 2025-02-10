package com.himedia.luckydokiapi.domain.product.repository;

import com.himedia.luckydokiapi.domain.product.entity.CategoryBridge;
import com.himedia.luckydokiapi.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoryBridgeRepository extends JpaRepository<CategoryBridge, Long> {

    @Modifying
    @Query("delete from CategoryBridge cb where cb.product = :product")
    void deleteByProduct(@Param("product") Product product);
}

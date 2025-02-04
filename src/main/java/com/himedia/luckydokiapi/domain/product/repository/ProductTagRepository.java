package com.himedia.luckydokiapi.domain.product.repository;


import com.himedia.luckydokiapi.domain.product.entity.ProductTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductTagRepository extends JpaRepository<ProductTag, Long> {
}

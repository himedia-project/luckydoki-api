package com.himedia.luckydokiapi.domain.product.repository;


import com.himedia.luckydokiapi.domain.product.entity.Product;
import com.himedia.luckydokiapi.domain.product.entity.ProductTag;
import com.himedia.luckydokiapi.domain.product.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductTagRepository extends JpaRepository<ProductTag, Long> {
    @Query("select t from ProductTag t where t.product.id =:id")
    List<ProductTag> findByProductId(@Param("id") Long id);

    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM ProductTag t WHERE t.tag = :tag AND t.product = :product")
    boolean existsByTagAndProduct(@Param("tag") Tag savedTag, @Param("product") Product product);

    @Modifying
    @Query("delete from ProductTag t where t.product = :product")
    void deleteByProduct(@Param("product") Product product);
}

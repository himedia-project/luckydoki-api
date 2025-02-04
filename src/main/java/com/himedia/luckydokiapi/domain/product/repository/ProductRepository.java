package com.himedia.luckydokiapi.domain.product.repository;



import com.himedia.luckydokiapi.domain.product.entity.Product;
import com.himedia.luckydokiapi.domain.product.repository.querydsl.ProductRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long>
        , ProductRepositoryCustom {


    @Modifying
    @Query("update Product p set p.delFlag = true where p.id = :id")
    void modifyDeleteFlag(@Param("id") Long id);
}

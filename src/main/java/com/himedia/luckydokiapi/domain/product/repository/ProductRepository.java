package com.himedia.luckydokiapi.domain.product.repository;



import com.himedia.luckydokiapi.domain.product.entity.Product;
import com.himedia.luckydokiapi.domain.product.enums.ProductApproval;
import com.himedia.luckydokiapi.domain.product.repository.querydsl.ProductRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>
        , ProductRepositoryCustom {
    
    // product 내 카테고리 존재 여부 확인
    @Query("select case when count(p) > 0 then true else false end from Product p where p.category.id = :categoryId")
    boolean existsByCategoryId(@Param("categoryId") Long categoryId);

    @Modifying
    @Query("update Product p set p.delFlag = true where p.id = :id")
    void modifyDeleteFlag(@Param("id") Long id);

    @Query("select p from Product p where p.delFlag = false and p.approvalStatus = 'Y' and p.shop.id = :shopId order by p.id desc")
    List<Product> findByShopId(@Param("shopId") Long shopId);

    @Query("SELECT p FROM Product p WHERE p.approvalStatus = :status and p.delFlag = false")
    List<Product> findByApprovalStatus(@Param("status") ProductApproval status);


}

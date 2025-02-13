package com.himedia.luckydokiapi.domain.order.repository;

import com.himedia.luckydokiapi.domain.order.entity.OrderItem;
import com.himedia.luckydokiapi.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("SELECT CASE WHEN COUNT(o) > 0 THEN TRUE ELSE FALSE END FROM OrderItem o WHERE o.product = :product")
    boolean existByProduct(@Param("product") Product product);
}

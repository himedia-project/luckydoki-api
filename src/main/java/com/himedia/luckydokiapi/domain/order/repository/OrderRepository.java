package com.himedia.luckydokiapi.domain.order.repository;

import com.himedia.luckydokiapi.domain.order.entity.Order;
import com.himedia.luckydokiapi.domain.order.repository.querydsl.OrderRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long>,
        OrderRepositoryCustom {

    @Query("SELECT o FROM Order o WHERE o.member.email = :email order by o.id desc")
    List<Order> findByEmail(@Param("email") String email);


    @Query("SELECT o FROM Order o WHERE o.code = :code")
    Optional<Order> findByCode(@Param("code") String orderId);

    @Query("SELECT COALESCE(SUM(o.totalPrice), 0) FROM Order o WHERE o.orderStatus = 'CONFIRM' AND o.orderDate BETWEEN :startOfDay AND :endOfDay")
    Long calculateTodayRevenue(LocalDateTime startOfDay, LocalDateTime endOfDay);
}

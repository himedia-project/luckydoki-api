package com.himedia.luckydokiapi.domain.order.repository;

import com.himedia.luckydokiapi.domain.order.entity.Order;
import com.himedia.luckydokiapi.domain.order.repository.querydsl.OrderRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long>,
        OrderRepositoryCustom {

    @Query("SELECT o FROM Order o WHERE o.member.email = :email order by o.id desc")
    List<Order> findByEmail(@Param("email") String email);


}

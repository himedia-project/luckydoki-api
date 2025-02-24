package com.himedia.luckydokiapi.domain.order.repository.querydsl;

import com.himedia.luckydokiapi.domain.order.controllor.AdminOrderController;
import com.himedia.luckydokiapi.domain.order.entity.Order;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

public interface OrderRepositoryCustom {

    Page<Order> findListBy(AdminOrderController.OrderHisRequestDTO requestDTO);

    Integer calculateMonthlyRevenue(LocalDateTime monthAgo, LocalDateTime now);

    Long calculateMonthlyTotalCount(LocalDateTime monthAgo, LocalDateTime now);
}

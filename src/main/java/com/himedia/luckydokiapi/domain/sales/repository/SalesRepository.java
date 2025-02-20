package com.himedia.luckydokiapi.domain.sales.repository;

import com.himedia.luckydokiapi.domain.order.entity.Order;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesRepository extends JpaRepository<Order, Long> {

  @Query(value =
      "SELECT o.order_date AS date, SUM(o.total_price) AS totalSales " +
          "FROM orders o " +
          "GROUP BY o.order_date " +
          "ORDER BY o.order_date ASC",
      nativeQuery = true)
  List<Object[]> findDailySalesNative();
}

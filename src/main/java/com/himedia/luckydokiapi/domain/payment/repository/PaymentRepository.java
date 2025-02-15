package com.himedia.luckydokiapi.domain.payment.repository;


import com.himedia.luckydokiapi.domain.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Query("SELECT pm FROM Payment pm WHERE pm.order.code = :orderId")
    Optional<Payment> findByOrderId(@Param("orderId") String orderId);
}

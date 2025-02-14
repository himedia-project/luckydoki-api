package com.himedia.luckydokiapi.domain.order.controllor;


import com.himedia.luckydokiapi.domain.order.dto.OrderDTO;
import com.himedia.luckydokiapi.domain.order.dto.OrderHistDTO;
import com.himedia.luckydokiapi.domain.order.service.OrderService;
import com.himedia.luckydokiapi.security.MemberDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 주문 처리
     *
     * @param memberDTO 로그인한 회원 정보
     * @param orderDTO  주문 정보 (수량 필드 없음)
     * @return 주문 ID
     */
    @PostMapping
    public ResponseEntity<?> order(
            @AuthenticationPrincipal MemberDTO memberDTO,
            @Valid @RequestBody OrderDTO orderDTO
    ) {
        log.info("order orderDTO: {}", orderDTO);
        log.info("order memberDTO: {}", memberDTO);
        Long orderId = orderService.order(orderDTO.getCouponId(), orderDTO.getCartItems(), memberDTO.getEmail());
        return ResponseEntity.ok("success order id: " + orderId);
    }

    /**
     * 주문 내역 조회
     *
     * @param memberDTO 로그인한 회원 정보
     * @return 주문 내역 목록
     */
    @GetMapping("/hist/list")
    public List<OrderHistDTO> orderHist(
            @AuthenticationPrincipal MemberDTO memberDTO
    ) {
        log.info("getOrders memberDTO: {}", memberDTO);
        return orderService.getOrders(memberDTO.getEmail());
    }

    /**
     * 주문 취소
     *
     * @param memberDTO 로그인한 회원 정보
     * @param orderId   주문 ID
     * @return 취소 결과
     */
    @PostMapping("{id}/cancel")
    public ResponseEntity<String> cancelOrder(
            @AuthenticationPrincipal MemberDTO memberDTO,
            @PathVariable("id") Long orderId
    ) {
        log.info("cancelOrder memberDTO: {}", memberDTO);
        log.info("cancelOrder orderId: {}", orderId);
        orderService.validateOrder(orderId, memberDTO.getEmail());
        orderService.cancelOrder(orderId, memberDTO.getEmail());
        return ResponseEntity.ok("success cancel order id: " + orderId);
    }
}

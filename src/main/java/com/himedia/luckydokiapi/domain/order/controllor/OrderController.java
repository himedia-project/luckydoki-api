package com.himedia.luckydokiapi.domain.order.controllor;


import com.himedia.luckydokiapi.domain.chat.dto.ChatRoomDTO;
import com.himedia.luckydokiapi.domain.order.dto.OrderDTO;
import com.himedia.luckydokiapi.domain.order.dto.OrderHistDTO;
import com.himedia.luckydokiapi.domain.order.service.OrderService;
import com.himedia.luckydokiapi.security.MemberDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

    @Operation(
            summary = "주문 처리 로직",
            description = "로그인 한 회원의 정보와 주문 정보를 받아 주문 번호를 return 합니다",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "장바구니 상품 목록과 쿠폰 정보 ",
                    required = true,
                    content = @Content(schema = @Schema(implementation = OrderDTO.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "주문 성공 "),
            }
    )
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
    @Operation(summary = "주문 내역 조회", description = "사용자의 인증 정보로 해당 사용자의 주문 정보를 조회합니다")
    @GetMapping("/hist/list")
    public List<OrderHistDTO> orderHist(@Parameter(description = "인증된 사용자 정보", hidden = true)
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
    @Operation(summary = "주문 취소", description = "사용자의 인증 정보와 주문 id로 주문을 취소합니다")
    @PostMapping("{id}/cancel")
    public ResponseEntity<String> cancelOrder(
            @Parameter(description = "인증된 사용자 정보", hidden = true)
            @AuthenticationPrincipal MemberDTO memberDTO,
            @Parameter(description = "주문 ID", required = true)
            @PathVariable("id") Long orderId
    ) {
        log.info("cancelOrder memberDTO: {}", memberDTO);
        log.info("cancelOrder orderId: {}", orderId);
        orderService.validateOrder(orderId, memberDTO.getEmail());
        orderService.cancelOrder(orderId, memberDTO.getEmail());
        return ResponseEntity.ok("success cancel order id: " + orderId);
    }
}

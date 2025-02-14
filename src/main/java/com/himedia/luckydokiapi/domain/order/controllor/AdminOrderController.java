package com.himedia.luckydokiapi.domain.order.controllor;

import com.himedia.luckydokiapi.domain.order.dto.OrderHistDTO;
import com.himedia.luckydokiapi.domain.order.service.OrderService;
import com.himedia.luckydokiapi.dto.PageRequestDTO;
import com.himedia.luckydokiapi.dto.PageResponseDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("/api/admin/order")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;


    @Setter
    @Getter
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class OrderHisRequestDTO extends PageRequestDTO {
        // 년도 ex. 2024, 2023, 2022 만약 null일시, 최근 6개월
        private String searchKeyword;
        private Integer year;
    }

    /**
     * 주문 목록
     *
     * @return 주문 내역 목록
     */
    @GetMapping("/list")
    public PageResponseDTO<OrderHistDTO> orderList(
            OrderHisRequestDTO orderHisRequestDTO
    ) {
        log.info("orderList orderHisRequestDTO: {}", orderHisRequestDTO);
        return orderService.list(orderHisRequestDTO);
    }


    /**
     * 주문 상세보기
     * @param orderId 주문 ID
     */
    @GetMapping("/{orderId}/detail")
    public OrderHistDTO orderDetail(
            @PathVariable Long orderId
    ) {
        log.info("orderDetail orderId: {}", orderId);
        return orderService.getOne(orderId);
    }
}

    package com.himedia.luckydokiapi.domain.order.service;



    import com.himedia.luckydokiapi.domain.cart.dto.CartItemDTO;
    import com.himedia.luckydokiapi.domain.order.controllor.AdminOrderController;
    import com.himedia.luckydokiapi.domain.order.dto.OrderHistDTO;
    import com.himedia.luckydokiapi.domain.order.entity.Order;
    import com.himedia.luckydokiapi.domain.product.entity.Product;
    import com.himedia.luckydokiapi.dto.PageResponseDTO;

    import java.util.List;

    public interface OrderService {

        /**
         * 주문 내역 조회
         * @param orderHisRequestDTO 주문 내역 조회 요청 DTO
         * @return 주문 내역 목록
         */
        PageResponseDTO<OrderHistDTO> list(AdminOrderController.OrderHisRequestDTO orderHisRequestDTO);

        /**
         * 주문 처리
         * @param cartItemDTOs 장바구니 상품 목록
         * @param email 회원 이메일
         * @return 주문 ID
         */
        Long order(Long couponId, List<CartItemDTO> cartItemDTOs, String email);

        /**
         * 주문 내역 조회
         * @param email 회원 이메일
         * @return 주문 내역 목록
         */
        List<OrderHistDTO> getOrders(String email);

        /**
         * 주문 유효성 검사
         * @param orderId 주문 ID
         * @param email 회원 이메일
         */
        void validateOrder(Long orderId, String email);

        /**
         * 주문 취소
         * @param orderId 주문 ID
         * @param email 회원 이메일
         */
        void cancelOrder(Long orderId, String email);

        /**
         * 상품 주문 가능 여부 확인
         * @param product 상품
         * @return 주문 가능 여부
         */
        boolean checkProductOrder(Product product);

        /**
         * 주문 상세 조회
         * @param orderId 주문 ID
         * @return 주문 상세 정보
         */
        OrderHistDTO getOne(Long orderId);

        /**
         * 주문 엔티티 조회
         * @param orderId 주문 ID(주문코드)
         * @return 주문 엔티티
         */
        Order getEntityByCode(String orderId);

        void removeCartItemsMatchedOrderItemsBy(List<OrderItem> orderItems);
    }
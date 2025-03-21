package com.himedia.luckydokiapi.domain.order.service;


import com.himedia.luckydokiapi.domain.cart.dto.CartItemDTO;
import com.himedia.luckydokiapi.domain.cart.entity.Cart;
import com.himedia.luckydokiapi.domain.cart.repository.CartItemRepository;
import com.himedia.luckydokiapi.domain.cart.service.CartService;
import com.himedia.luckydokiapi.domain.coupon.entity.Coupon;
import com.himedia.luckydokiapi.domain.coupon.service.CouponService;
import com.himedia.luckydokiapi.domain.member.entity.Member;
import com.himedia.luckydokiapi.domain.member.service.MemberService;
import com.himedia.luckydokiapi.domain.order.controllor.AdminOrderController;
import com.himedia.luckydokiapi.domain.order.dto.OrderHistDTO;
import com.himedia.luckydokiapi.domain.order.entity.Order;
import com.himedia.luckydokiapi.domain.order.entity.OrderItem;
import com.himedia.luckydokiapi.domain.order.repository.OrderItemRepository;
import com.himedia.luckydokiapi.domain.order.repository.OrderRepository;
import com.himedia.luckydokiapi.domain.product.entity.Product;
import com.himedia.luckydokiapi.domain.product.service.ProductService;
import com.himedia.luckydokiapi.dto.PageResponseDTO;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    private final ProductService productService;
    private final MemberService memberService;
    private final CartItemRepository cartItemRepository;
    private final CartService cartService;
    private final CouponService couponService;


    @Transactional(readOnly = true)
    @Override
    public PageResponseDTO<OrderHistDTO> list(AdminOrderController.OrderHisRequestDTO requestDTO) {
        log.info("list..............");

        Page<Order> result = orderRepository.findListBy(requestDTO);

        List<OrderHistDTO> dtoList = new ArrayList<>();

        for (Order order : result) {
            OrderHistDTO orderHistDTO = this.createOrderHistDTO(order);
            dtoList.add(orderHistDTO);
        }

        return PageResponseDTO.<OrderHistDTO>withAll()
                .dtoList(dtoList)
                .totalCount(result.getTotalElements())
                .pageRequestDTO(requestDTO)
                .build();
    }


    @Override
    public Long order(Long couponId, List<CartItemDTO> cartItemDTOs, String email) {
        // 회원 정보 조회
        Member member = memberService.getEntity(email);

        List<OrderItem> orderItemList = new ArrayList<>();

        for (CartItemDTO cartItemDTO : cartItemDTOs) {
            log.info("cartItemDTO: {}", cartItemDTO);
            // 상품 정보 조회
            Product product = productService.getEntity(cartItemDTO.getProductId());
            // 주문 아이템 생성 (수량 정보만큼)
            OrderItem orderItem = OrderItem.from(product, cartItemDTO.getQty());
            orderItemList.add(orderItem);
        }
        // 총 할인 금액 계산
        int totalDiscountPrice = 0;
        // 주문에 사용한 쿠폰
        Coupon useCoupon = null;

        // 쿠폰이 있는 경우의 처리
        if (couponId != null) {
            Coupon coupon = couponService.getCoupon(couponId);
            int couponDiscountPrice = coupon.getDiscountPrice();
            
            // 총 주문금액이 coupon minimumUsageAmount(쿠폰 사용 최소 금액)보다 작으면 예외처리
//            if (coupon.getMinimumUsageAmount() != null && savedOrder.getCalcTotalPrice() < coupon.getMinimumUsageAmount()) {
//                throw new IllegalArgumentException("쿠폰 사용 최소 금액 이상만 쿠폰적용이 됩니다. 주문 총금액 : " + savedOrder.getCalcTotalPrice() + " , 쿠폰 사용 최소 금액 : " + coupon.getMinimumUsageAmount());
//            }

            totalDiscountPrice = couponDiscountPrice;  // 지금 현재 할인금액 쿠폰 밖에 없음!
            useCoupon = coupon;
        }

        // 주문 생성
        Order newOrder = Order.from(member, useCoupon, orderItemList, totalDiscountPrice);
        log.info("new Order with code: {}, productsPrice: {}", newOrder.getCode(), newOrder.getTotalPrice());

        // 주문 저장
        Order savedOrder = orderRepository.save(newOrder);
        return savedOrder.getId();
    }

    @Override
    public List<OrderHistDTO> getOrders(String email) {
        log.info("getOrders email: {}", email);
        List<Order> orders = orderRepository.findByEmail(email);
        List<OrderHistDTO> orderHistDTOs = new ArrayList<>();

        // 주문 내역 조회 orderHistDTOs 에 담기
        for (Order order : orders) {
            OrderHistDTO orderHistDTO = this.createOrderHistDTO(order);
            orderHistDTOs.add(orderHistDTO);
        }
        return orderHistDTOs;
    }

    @Override
    public void validateOrder(Long orderId, String email) {
        Order order = getEntity(orderId);
        Member curMember = memberService.getEntity(email);
        if (!curMember.getEmail().equals(order.getMember().getEmail())) {
            throw new IllegalArgumentException("해당 주문한 고객이 아닙니다!");
        }
    }

    @Override
    public void cancelOrder(Long orderId, String email) {
        Order order = getEntity(orderId);
        order.cancelOrder(); // 주문 취소
    }

    @Override
    public boolean checkProductOrder(Product product) {
        return orderItemRepository.existByProduct(product);
    }

    @Transactional(readOnly = true)
    @Override
    public OrderHistDTO getOne(Long orderId) {
        return this.createOrderHistDTO(this.getEntity(orderId));
    }

    @Transactional(readOnly = true)
    @Override
    public Order getEntityByCode(String orderId) {
        return orderRepository.findByCode(orderId)
                .orElseThrow(() -> new EntityNotFoundException("해당 주문이 없습니다."));
    }


    // 주문 조회
    private Order getEntity(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("해당 주문이 없습니다."));
    }

    // Order -> OrderHistDTO 변환
    private OrderHistDTO createOrderHistDTO(Order order) {
        return OrderHistDTO.from(order);
    }


    /**
     * 주문 완료후, 장바구니로 주문하는 거라면, 해당 장바구니 cart items 삭제
     * @param orderItems 주문 아이템 리스트
     */
    @Override
    public void removeCartItemsMatchedOrderItemsBy(Cart cart, List<OrderItem> orderItems) {
        // 주문 후, 장바구니로 주문하는 거라면, 해당 장바구니 cart items 삭제
        orderItems.forEach(orderItem -> {
            // 만일 장바구니에 해당 상품이 없으면,
            if (!cartItemRepository.existByCartIdAndProductId(cart.getId(), orderItem.getProduct().getId())) {
                return;
            }
            // 만일 장바구니에 해당 상품이 있으면, 해당 상품 삭제
            cartItemRepository.delete(
                    cartItemRepository.findByCartIdAndProductId(cart.getId(), orderItem.getProduct().getId())
                            .orElseThrow(() -> new EntityNotFoundException("해당 상품이 장바구니에 없습니다. cartId: " + cart.getId() + ", productId: " + orderItem.getProduct().getId()))
            );
        });
    }
}

package com.himedia.luckydokiapi.domain.order.service;


import com.himedia.luckydokiapi.domain.cart.dto.CartItemDTO;
import com.himedia.luckydokiapi.domain.cart.repository.CartItemRepository;
import com.himedia.luckydokiapi.domain.cart.service.CartService;
import com.himedia.luckydokiapi.domain.member.entity.Member;
import com.himedia.luckydokiapi.domain.member.repository.MemberRepository;
import com.himedia.luckydokiapi.domain.order.dto.OrderHistDTO;
import com.himedia.luckydokiapi.domain.order.entity.Order;
import com.himedia.luckydokiapi.domain.order.entity.OrderItem;
import com.himedia.luckydokiapi.domain.order.repository.OrderRepository;
import com.himedia.luckydokiapi.domain.product.entity.Product;
import com.himedia.luckydokiapi.domain.product.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    private final CartItemRepository cartItemRepository;
    private final CartService cartService;

    @Override
    public Long order(List<CartItemDTO> cartItemDTOs, String email) {
        // 회원 정보 조회
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("해당 회원이 없습니다. email: " + email));

        List<OrderItem> orderItemList = new ArrayList<>();

        for (CartItemDTO cartItemDTO : cartItemDTOs) {
            log.info("cartItemDTO: {}", cartItemDTO);

            // 상품 정보 조회
            Product product = productRepository.findById(cartItemDTO.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("해당 상품이 없습니다. productId: " + cartItemDTO.getProductId()));

            // 주문 아이템 생성 (수량 정보는 없음)
            OrderItem orderItem = OrderItem.createOrderItem(product); // 수량을 1로 고정
            orderItemList.add(orderItem);
        }

        // 주문 생성
        Order savedOrder = orderRepository.save(Order.createOrder(member, orderItemList));
        log.info("Order created with code: {}", savedOrder.getCode()); // 생성된 주문 코드 로그 출력
        // 주문 후, 장바구니로 주문하는 거라면, 해당 장바구니 cart items 삭제
        cartService.getCartItemList(email).forEach(cartItemDTO -> {
            cartItemRepository.delete(cartItemRepository.findByProductId(cartItemDTO.getProductId()));
        });

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
        Order order = findOrder(orderId);
        Member curMember = memberRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("해당 회원이 없습니다."));
        if (!curMember.getEmail().equals(order.getMember().getEmail())) {
            throw new IllegalArgumentException("해당 주문한 고객이 아닙니다!");
        }
    }

    @Override
    public void cancelOrder(Long orderId, String email) {
        Order order = findOrder(orderId);
        order.cancelOrder(); // 주문 취소
    }

    // 주문 조회
    private Order findOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("해당 주문이 없습니다."));
    }

    // Order -> OrderHistDTO 변환
    private OrderHistDTO createOrderHistDTO(Order order) {
        return OrderHistDTO.from(order);
    }
}

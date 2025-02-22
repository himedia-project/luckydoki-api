package com.himedia.luckydokiapi.domain.dashboard.service;

import com.himedia.luckydokiapi.domain.dashboard.dto.DashboardDTO;
import com.himedia.luckydokiapi.domain.member.repository.MemberRepository;
import com.himedia.luckydokiapi.domain.order.enums.OrderStatus;
import com.himedia.luckydokiapi.domain.order.repository.OrderRepository;
import com.himedia.luckydokiapi.domain.product.dto.ProductDTO;
import com.himedia.luckydokiapi.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class AdminDashBoardServiceImpl implements AdminDashBoardService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    @Override
    public DashboardDTO getDashBoard() {

        LocalDateTime now = LocalDateTime.now();            // 현재 시간
        LocalDateTime monthAgo = now.minusMonths(1);        // 1달 전
        LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();  // 오늘 0시
        LocalDateTime endOfDay = startOfDay.plusDays(1);    // 오늘 24시

        // 총 주문수
        Long totalOrderCount = orderRepository.findAll().stream()
                .filter(order -> order.getOrderStatus() == OrderStatus.CONFIRM).count();
        // 오늘의 매출
        Long todayRevenue = orderRepository.calculateTodayRevenue(startOfDay, endOfDay);

        // 한달 내 신규 회원수
        Long newMemberCount = memberRepository.findAll().stream()
                .filter(member -> member.getCreatedAt().isAfter(monthAgo)).count();

        // 총 상품 등록 수
        Long totalProductCount = productRepository.count();

        // 인기 상품 Top 10 (기준: 좋아요수, 구매수)
         List<ProductDTO.Response> topProducts = productRepository.findTop10ByOrderByLikeCountDesc().stream()
                .map(ProductDTO.Response::from).toList();

        return DashboardDTO.builder()
                .totalOrderCount(totalOrderCount)
                .todayRevenue(todayRevenue)
                .newMemberCount(newMemberCount)
                .totalProductCount(totalProductCount)
                .topProducts(topProducts)
                .build();
    }
}

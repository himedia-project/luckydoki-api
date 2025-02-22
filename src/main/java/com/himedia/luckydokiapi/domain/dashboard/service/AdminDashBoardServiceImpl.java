package com.himedia.luckydokiapi.domain.dashboard.service;

import com.himedia.luckydokiapi.domain.dashboard.dto.DashboardDTO;
import com.himedia.luckydokiapi.domain.member.dto.MemberDetailDTO;
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

        // 최근 한달간 총 주문수
        Long totalOrderCount = orderRepository.findAll().stream()
                .filter(order -> order.getOrderStatus() == OrderStatus.CONFIRM 
                        && order.getOrderDate().isAfter(monthAgo))
                .count();
        // 오늘의 매출
        Long todayRevenue = orderRepository.calculateTodayRevenue(startOfDay, endOfDay);

        // 한달 내 신규 회원수
        Long newMemberCount = memberRepository.findAll().stream()
                .filter(member -> member.getCreatedAt().isAfter(monthAgo)).count();

        // 총 상품 등록 수
        Long totalProductCount = productRepository.count();

        // 인기 상품 Top 10 (기준: 리뷰평점 + 리뷰수 + 좋아요수, 구매수)
         List<ProductDTO.Response> top10Products = productRepository.findTop10ByOrderByLikeCountAndReviewCountDesc().stream()
                .map(ProductDTO.Response::from).toList();

         // top 5 sellers(많이 판매한)
        List<MemberDetailDTO> top5Sellers = memberRepository.findTop5Sellers().stream()
                .map(MemberDetailDTO::from).toList();
        // top 5 GoodConsumer(많이 구매하고 && review를 content를 10자 이상 쓴)
        List<MemberDetailDTO> top5GoodConsumers = memberRepository.findTop5GoodConsumers().stream()
                .map(MemberDetailDTO::from).toList();

        // 승인 안된 셀러 신청 수
        Long sellerNotApprovedRequestCount = memberRepository.countBySellerApprovedIsFalse();

        return DashboardDTO.builder()
                .totalOrderCount(totalOrderCount)
                .todayRevenue(todayRevenue)
                .newMemberCount(newMemberCount)
                .totalProductCount(totalProductCount)
                .top10Products(top10Products)
                .top5Sellers(top5Sellers)
                .top5GoodConsumers(top5GoodConsumers)
                .sellerNotApprovedRequestCount(sellerNotApprovedRequestCount)
                .build();
    }
}

package com.himedia.luckydokiapi.domain.dashboard.service;

import com.himedia.luckydokiapi.domain.community.dto.CommunityResponseDTO;
import com.himedia.luckydokiapi.domain.community.repository.CommunityRepository;
import com.himedia.luckydokiapi.domain.dashboard.dto.DashboardDTO;
import com.himedia.luckydokiapi.domain.member.dto.MemberDetailDTO;
import com.himedia.luckydokiapi.domain.member.repository.MemberRepository;
import com.himedia.luckydokiapi.domain.order.enums.OrderStatus;
import com.himedia.luckydokiapi.domain.order.repository.OrderRepository;
import com.himedia.luckydokiapi.domain.product.dto.ProductDTO;
import com.himedia.luckydokiapi.domain.product.enums.ProductApproval;
import com.himedia.luckydokiapi.domain.product.repository.ProductRepository;
import com.himedia.luckydokiapi.domain.sales.dto.SalesData;
import com.himedia.luckydokiapi.domain.sales.service.SalesService;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
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
    private final CommunityRepository communityRepository;
    private final SalesService salesService;

    @Transactional(readOnly = true)
    @Override
    public DashboardDTO getDashBoard() {

        LocalDateTime now = LocalDateTime.now();            // 현재 시간
        LocalDateTime monthAgo = now.minusMonths(1);        // 1달 전
        LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();  // 오늘 0시
        LocalDateTime endOfDay = startOfDay.plusDays(1);    // 오늘 24시

        // 최근 한달간 총 주문수
        Long totalOrderCount = orderRepository.calculateMonthlyTotalCount(monthAgo, now);


        // 최근 한달간 총 매출
        Integer monthlyRevenue = orderRepository.calculateMonthlyRevenue(monthAgo, now);

        // 오늘의 매출
        Long todayRevenue = orderRepository.calculateTodayRevenue(startOfDay, endOfDay);

        // 한달 내 신규 회원수
        Long newMemberCount = memberRepository.findAll().stream()
                .filter(member -> member.getCreatedAt().isAfter(monthAgo)).count();

        // 한달 내 신규 셀러수
        Long newSellerCount = memberRepository.countNewSellersInLastMonth(monthAgo);

        // 총 상품(승인 o && 삭제여부=false) 등록 수
        Long totalProductCount = productRepository.findAll().stream()
                .filter(product -> product.getApprovalStatus() == ProductApproval.Y && !product.getDelFlag())
                .count();
        // 총 커뮤니티 게시글 수
        Long totalCommunityCount = communityRepository.count();

        // 인기 상품 Top 10 (기준: 리뷰평점(평균 평점 × 2) + 리뷰 수 + 좋아요 수 + 주문 수)
        List<ProductDTO.Response> top10Products = productRepository.findTop10ByOrderByLikeCountAndReviewCountDesc().stream()
                .map(ProductDTO.Response::from).toList();

        // 인기 커뮤니티 게시글 Top 10 (답글 수)
        List<CommunityResponseDTO> top10Communities = communityRepository.findTop10ByOrderByLikeCountAndCommentCountDesc().stream()
                .map(CommunityResponseDTO::from).toList();

        // top 5 sellers(좋아요 수 + 판매량)
        List<MemberDetailDTO> top5Sellers = memberRepository.findTop5Sellers().stream()
                .map(MemberDetailDTO::from).toList();
        // top 5 GoodConsumer(많이 구매하고 && review를 content를 10자 이상 쓴)
        List<MemberDetailDTO> top5GoodConsumers = memberRepository.findTop5GoodConsumers().stream()
                .map(MemberDetailDTO::from).toList();

        // 승인 안된 셀러 신청 수
        Long sellerNotApprovedRequestCount = memberRepository.countBySellerApprovedIsFalse();

        // SalesService를 통해 일별 매출 데이터 조회
        List<SalesData> dailySalesData = salesService.getDailySalesData();

        // 최신 날짜의 시간별 매출 데이터 조회 (SalesData의 날짜는 LocalDateTime)
        List<SalesData> hourlySalesData = Collections.emptyList();
        if (!dailySalesData.isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            // 최신 일자의 문자열(yyyy-MM-dd)
            String latestDate = dailySalesData.get(dailySalesData.size() - 1).getDate().format(formatter);
            hourlySalesData = salesService.getSalesDataByDate(latestDate);
        }

        return DashboardDTO.builder()
                .totalOrderCount(totalOrderCount)
                .monthlyRevenue(monthlyRevenue)
                .todayRevenue(todayRevenue)
                .newMemberCount(newMemberCount)
                .newSellerCount(newSellerCount)
                .totalProductCount(totalProductCount)
                .totalCommunityCount(totalCommunityCount)
                .top10Products(top10Products)
                .top10Communities(top10Communities)
                .top5Sellers(top5Sellers)
                .top5GoodConsumers(top5GoodConsumers)
                .sellerNotApprovedRequestCount(sellerNotApprovedRequestCount)
                .dailySalesData(dailySalesData)
                .hourlySalesData(hourlySalesData)
                .build();
    }
}

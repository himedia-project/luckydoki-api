package com.himedia.luckydokiapi.domain.dashboard.dto;

import com.himedia.luckydokiapi.domain.community.dto.CommunityResponseDTO;
import com.himedia.luckydokiapi.domain.member.dto.MemberDetailDTO;
import com.himedia.luckydokiapi.domain.product.dto.ProductDTO;
import com.himedia.luckydokiapi.domain.sales.dto.SalesData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DashboardDTO {

    private Long totalOrderCount;        // 총 주문 수(최근 30일)
    private Long todayRevenue;           // 오늘의 매출
    private int monthlyRevenue;          // 이번 달 매출
    private Long newMemberCount;         // 신규 회원 수 (1달 내)
    private Long newSellerCount;         // 신규 셀러 수 (1달 내)
    private Long totalProductCount;      // 총 상품 등록 수
    private Long totalCommunityCount;    // 총 커뮤니티 게시글 수

    private List<ProductDTO.Response> top10Products;     // 인기 상품 Top 10
    private List<CommunityResponseDTO> top10Communities; // 인기 커뮤니티 Top 10

    // top 5 sellers
    private List<MemberDetailDTO> top5Sellers;
    // top 5 buyers
    private List<MemberDetailDTO> top5GoodConsumers;

    private Long sellerNotApprovedRequestCount; // 셀러 신청 수

    private List<SalesData> dailySalesData;

    private List<SalesData> hourlySalesData;
}

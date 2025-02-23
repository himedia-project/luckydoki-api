package com.himedia.luckydokiapi.domain.member.dto;

import com.himedia.luckydokiapi.domain.member.entity.Member;
import com.himedia.luckydokiapi.domain.member.enums.MemberRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class MemberDetailDTO {

    private String email;
    private String phone;
    private String nickName;
    private String profileImage;
    private String roleName;
    private Long shopId;
    private String shopImage;
    private boolean sellerRequested; // seller_application 여부
    private Long activeCouponCount;  // 사용가능한 쿠폰 수
    private Long monthlySales;       // 월간 판매액
    private Long monthlyPurchase;   // 월간 구매액
    private Long reviewCount;       // 리뷰 등록 수

    public static MemberDetailDTO from(Member member) {
        return MemberDetailDTO.builder()
                .email(member.getEmail())
                .phone(member.getPhone())
                .nickName(member.getNickName())
                .profileImage(member.getProfileImage())
                .roleName(member.getMemberRoleList().stream().map(MemberRole::getRoleName).findFirst().orElse(null))
                .shopId(member.getShop() == null ? null : member.getShop().getId())
                .shopImage(member.getShop() == null ? null : member.getShop().getImage())
                .activeCouponCount(member.getActiveCouponCount())
                .monthlySales(member.getMonthlySales())
                .monthlyPurchase(member.getMonthlyPurchase())
                .reviewCount(member.getReviewCount())
                .build();
    }
}

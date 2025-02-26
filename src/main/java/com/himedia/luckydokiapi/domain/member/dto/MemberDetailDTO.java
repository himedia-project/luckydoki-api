package com.himedia.luckydokiapi.domain.member.dto;

import com.himedia.luckydokiapi.domain.member.entity.Member;
import com.himedia.luckydokiapi.domain.member.enums.MemberRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Schema(description = "회원 정보 조회와 수정을 위한 dto ")
public class MemberDetailDTO {

    private String email;
    private String phone;
    private String nickName;
    private String profileImage;
    private String roleName;
    private Long shopId;
    private String shopImage;
    @Schema(description = "seller_application 여부")
    private boolean sellerRequested;
    @Schema(description = "사용가능한 쿠폰 수")
    private Long activeCouponCount;
    @Schema(description = "월간 판매액")
    private Long monthlySales;
    @Schema(description = "월간 구매액")
    private Long monthlyPurchase;
    @Schema(description = "리뷰 등록 수")
    private Long reviewCount;

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

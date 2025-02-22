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
    private String roleName;
    private Long shopId;
    private boolean sellerRequested; // seller_application 여부
    private Long activeCouponCount;  // 사용가능한 쿠폰 수

    public static MemberDetailDTO from(Member member) {
        return MemberDetailDTO.builder()
                .email(member.getEmail())
                .phone(member.getPhone())
                .nickName(member.getNickName())
                .roleName(member.getMemberRoleList().stream().map(MemberRole::getRoleName).findFirst().orElse(null))
                .shopId(member.getShop() == null ? null : member.getShop().getId())
                .activeCouponCount(member.getActiveCouponCount())
                .build();
    }
}

package com.himedia.luckydokiapi.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

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
    private boolean sellerUpgraded;
}

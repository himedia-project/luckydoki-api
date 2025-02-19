package com.himedia.luckydokiapi.domain.member.dto;

import lombok.*;


@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateMemberDTO {
    private String nickName;
    private String phone;
}

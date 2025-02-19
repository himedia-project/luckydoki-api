package com.himedia.luckydokiapi.domain.member.dto;

import com.himedia.luckydokiapi.domain.member.enums.MemberActive;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SocialLoginResponseDTO {
    private String email;
    private String nickName;
    private List<String> roles;
    private String accessToken;
    private MemberActive active;
}

package com.himedia.luckydokiapi.domain.member.dto;

import com.himedia.luckydokiapi.domain.member.enums.MemberActive;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data

@Schema(description = "로그인 성공 시 응답하는 회원 정보가 담긴 dto")
public class LoginResponseDTO {

    private String email;
    private String nickName;
    private List<String> roles;
    private String accessToken;
    private String refreshToken;        // 앱 전용 refresh token
    private MemberActive active;

}

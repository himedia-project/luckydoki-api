package com.himedia.luckydokiapi.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SocialLoginRequestDTO {

    @NotBlank(message = "이메일은 필수 입력값입니다.")
    private String email;

    private String nickName;

}

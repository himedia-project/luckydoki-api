package com.himedia.luckydokiapi.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "로그인 요청 DTO")
public class LoginRequestDTO {
    @Schema(description = "이메일",
            example = "admin@test.com, user1@test.com, seller1@test.com")
    @NotBlank(message = "이메일을 입력해주세요")
    private String email;

    @Schema(description = "비밀번호", example = "1234")
    @NotBlank(message = "패스워드를  입력해주세요")
    private String password;
}

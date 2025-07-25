package com.himedia.luckydokiapi.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Schema(description = "로그인 요청 DTO")
public class LoginRequestDTO {
    @Schema(description = "이메일",
            example = "admin@test.com, user1@test.com, seller1@test.com")
    @NotBlank(message = "이메일을 입력해주세요")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
            message = "이메일 형식이 올바르지 않습니다")
    private String email;

    @Schema(description = "비밀번호", example = "1234")
    @NotBlank(message = "패스워드를  입력해주세요")
    private String password;
}

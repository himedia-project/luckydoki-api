package com.himedia.luckydokiapi.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDTO {
    @NotBlank(message = "이메일을 입력해주세요")
    private String email;
    @NotBlank(message = "패스워드를  입력해주세요")
    private String password;
}

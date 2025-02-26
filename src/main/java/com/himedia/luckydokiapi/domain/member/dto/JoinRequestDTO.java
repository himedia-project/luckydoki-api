package com.himedia.luckydokiapi.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@Schema(description = "회원가입 요청 DTO")
public class JoinRequestDTO {
    @NotBlank(message = "이메일 필수 입력 항목 입니다")
    private String email; //username
    @NotBlank(message = "닉네임은 필수 입력 항목 입니다")
    private String nickName;
//    @NotNull(message = "생년월일 필수 입력 값입니다.")
    private Long birthday;
    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    private String password;
    @NotBlank(message = "전화번호는 필수 입력 값입니다.")
    private String phone;
    @NotBlank(message = "전화번호 인증 코드를 입력하세요")
    private String verificationCode;

    private String fcmToken;
}
package com.himedia.luckydokiapi.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class JoinRequestDTO {
    @NotBlank(message = "이메일 필수 입력 항목 입니다")
    private String email; //username
    @NotBlank(message = "이름은 필수 입력 항목 입니다")
    private String name;
    @NotNull(message = "생년월일 필수 입력 값입니다.")
    private Long birthday;
    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    private String password;
    @NotBlank(message = "전화번호는 필수 입력 값입니다.")
    private String phone;

    private String fcmToken;

}

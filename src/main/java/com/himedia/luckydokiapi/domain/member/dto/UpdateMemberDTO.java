package com.himedia.luckydokiapi.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


@SuperBuilder
@Getter
@NoArgsConstructor
@NotBlank

public class UpdateMemberDTO {
    private String nickName;
    private String phone;
}

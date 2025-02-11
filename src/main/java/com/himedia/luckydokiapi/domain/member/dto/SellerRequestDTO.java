package com.himedia.luckydokiapi.domain.member.dto;

import com.himedia.luckydokiapi.domain.member.enums.MemberRole;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SellerRequestDTO {
    @NotBlank
    private String email;

    @NotBlank
    private String nickName;

    @NotBlank
    private String profileImage;

    @NotBlank
    private String introduction;
}

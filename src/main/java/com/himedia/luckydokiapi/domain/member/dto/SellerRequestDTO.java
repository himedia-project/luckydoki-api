package com.himedia.luckydokiapi.domain.member.dto;

import com.himedia.luckydokiapi.domain.member.enums.MemberRole;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellerRequestDTO {
    @NotBlank
    private String email;

    @NotBlank
    private String nickName;
}

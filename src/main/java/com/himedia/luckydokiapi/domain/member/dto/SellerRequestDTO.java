package com.himedia.luckydokiapi.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SellerRequestDTO {


    @NotBlank
    private String nickName;

    @NotBlank
    private String profileImage;

    @NotBlank
    private String introduction;
}

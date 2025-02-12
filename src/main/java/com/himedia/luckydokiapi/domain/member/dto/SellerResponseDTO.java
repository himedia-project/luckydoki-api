package com.himedia.luckydokiapi.domain.member.dto;

import com.himedia.luckydokiapi.domain.member.enums.ShopApproved;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor

public class SellerResponseDTO {

    private Long id;
    private String email;
    private String nickName;
    private ShopApproved approved;
    private String statusDescription;
    private String shopImage;
    private String introduction;


}

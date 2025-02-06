package com.himedia.luckydokiapi.domain.member.dto;

import com.himedia.luckydokiapi.domain.member.entity.SellerApplication;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class SellerResponseDTO {

    private Long id;
    private String email;
    private String nickName;
    private boolean isApproved;
    private String statusDescription;


}

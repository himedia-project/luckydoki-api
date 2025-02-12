package com.himedia.luckydokiapi.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.himedia.luckydokiapi.domain.member.enums.ShopApproved;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime requestAt;


}

package com.himedia.luckydokiapi.domain.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FcmTokenRequestDTO {

    private String fcmToken;
    private String email;
}
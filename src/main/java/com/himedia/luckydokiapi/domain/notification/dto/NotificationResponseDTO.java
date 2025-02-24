package com.himedia.luckydokiapi.domain.notification.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.himedia.luckydokiapi.domain.notification.entity.Notification;
import com.himedia.luckydokiapi.domain.notification.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponseDTO {

    private String shopImage;
    private String title;
    private String body;
    private NotificationType type;
    private String targetEmail;
    private String fcmToken;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime timestamp;

    public static NotificationResponseDTO from(Notification notification) {
        return NotificationResponseDTO.builder()
                .shopImage(notification.getMember().getShop() == null ? notification.getMember().getProfileImage() : notification.getMember().getShop().getImage())
                .targetEmail(notification.getMember().getEmail())
                .title(notification.getTitle())
                .body(notification.getBody())
                .type(notification.getType())
                .fcmToken(notification.getFcmToken())
                .timestamp(notification.getCreatedAt())
                .build();
    }
}

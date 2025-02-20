package com.himedia.luckydokiapi.domain.chat.dto;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@ToString
public class MessageNotificationDTO {

    private String notificationMessage;
    private Long roomId;
    private LocalDateTime timestamp;
    private String sender; //보낸 사람
    private String email; //받는사람
    private boolean isRead;
    private String shopImages;

}

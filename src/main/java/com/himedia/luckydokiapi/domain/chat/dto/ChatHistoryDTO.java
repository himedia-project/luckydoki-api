package com.himedia.luckydokiapi.domain.chat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class ChatHistoryDTO {
    private Long roomId;
    private String email;
    private Long shopId;
    private String shopImage;
    private String message;     // 채팅 내용

//    @JsonFormat( pattern = "yyyy-MM-dd' 'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime lastMessageTime; //마지막 대화 시간
}

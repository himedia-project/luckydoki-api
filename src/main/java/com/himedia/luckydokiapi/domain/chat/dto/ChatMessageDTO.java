package com.himedia.luckydokiapi.domain.chat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ChatMessageDTO {
    private Long roomId;
    private String sender; //보낸이 : 서버에서 다시 한번 인증한뒤 값을 넣는다
    private String email;
    private Long ShopId;
    private String message;

    private LocalDateTime sendTime;



}

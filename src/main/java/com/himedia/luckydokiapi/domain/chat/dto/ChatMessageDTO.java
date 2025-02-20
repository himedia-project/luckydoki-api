package com.himedia.luckydokiapi.domain.chat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.web.multipart.MultipartFile;

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
    private String sender;
    private String email;
    private Long ShopId;
    private String message;
    private LocalDateTime sendTime;

 }

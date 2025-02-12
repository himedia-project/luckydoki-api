package com.himedia.luckydokiapi.domain.chat.dto;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ChatMessageDTO {
    private Long roomId;
    private String sender;
    private String buyerEmail;
    private String sellerEmail;
    private Long productId;     // 상품 ID
    private String message;     // 채팅 내용
    private LocalDateTime sendDate;
}

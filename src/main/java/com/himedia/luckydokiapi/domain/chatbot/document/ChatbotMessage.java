package com.himedia.luckydokiapi.domain.chatbot.document;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Document(collection = "chatbot_message")
public class ChatbotMessage {
    
    @Id
    private String id;
    private String chatRoomId;
    private String email;        // 메시지 보낸 사람의 이메일 (챗봇인 경우 "chatbot")
    private String message;      // 메시지 내용
    private LocalDateTime timestamp;  // 메시지 시간
    
    @Builder
    public ChatbotMessage(String chatRoomId, String email, String message, LocalDateTime timestamp) {
        this.chatRoomId = chatRoomId;
        this.email = email;
        this.message = message;
        this.timestamp = timestamp;
    }
} 
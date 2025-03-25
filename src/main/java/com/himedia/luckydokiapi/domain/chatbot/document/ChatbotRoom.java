package com.himedia.luckydokiapi.domain.chatbot.document;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@Document(collection = "chatbot_room")
public class ChatbotRoom {
    
    @Id
    private String id;
    private String userEmail;    // null 또는 빈 문자열 가능
    private String sessionId;    // 비로그인 사용자 식별용
    private LocalDateTime startTime;
    private LocalDateTime lastResponseTime;
    private String lastMessage;
    private boolean active;  // 채팅방 활성화 상태
    
    @Builder
    public ChatbotRoom(String userEmail) {
        this.userEmail = (userEmail == null || userEmail.trim().isEmpty()) ? null : userEmail.trim();
        this.sessionId = UUID.randomUUID().toString();
        this.startTime = LocalDateTime.now();
        this.lastResponseTime = this.startTime;
        this.active = true;
    }
    
    public void updateLastInteraction(String message, LocalDateTime responseTime) {
        this.lastMessage = message;
        this.lastResponseTime = responseTime;
    }

    public void deactivate() {
        this.active = false;
    }

    public boolean isAnonymous() {
        return userEmail == null || userEmail.trim().isEmpty();
    }
} 
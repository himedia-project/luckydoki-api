package com.himedia.luckydokiapi.interceptor;

import com.himedia.luckydokiapi.security.service.TokenValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class WebSocketAuthChannelInterceptor implements ChannelInterceptor {
    private final TokenValidationService tokenValidationService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            try {
                // 헤더에서 토큰 추출
                String authHeader = accessor.getFirstNativeHeader("X-Authorization");
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    String token = authHeader.substring(7);
                    
                    // 토큰 검증 및 인증 객체 생성
                    Authentication authentication = tokenValidationService.validateTokenAndCreateAuthentication(token);
                    
                    // WebSocket 연결에 인증 정보 설정
                    accessor.setUser(authentication);
                    log.info("WebSocket 연결 인증 성공: {}", authentication.getName());
                }
            } catch (Exception e) {
                log.error("WebSocket 연결 인증 실패: {}", e.getMessage());
                throw new RuntimeException("WebSocket 연결 인증 실패: " + e.getMessage());
            }
        }
        return message;
    }
}
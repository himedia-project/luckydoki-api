package com.himedia.luckydokiapi.interceptor;

import com.himedia.luckydokiapi.domain.member.enums.MemberActive;
import com.himedia.luckydokiapi.security.MemberDTO;
import com.himedia.luckydokiapi.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Component
public class WebSocketAuthChannelInterceptor implements ChannelInterceptor {
    //stomp 헤더에서 토큰을 꺼내와 인증 하는 클래스
    @Autowired
    private JWTUtil jwtUtil;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = accessor.getFirstNativeHeader("X-Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);

                // 토큰 검증
                Map<String, Object> claims = jwtUtil.validateToken(token);

                // claims에서 필요한 정보를 추출하여 Authentication 객체 생성
                String activeStr = (String) claims.get("active");
                MemberActive active = activeStr != null ? MemberActive.valueOf(activeStr) : null;

                MemberDTO member = new MemberDTO(
                        (String) claims.get("email"),
                        (String) claims.get("password"),
                        (String) claims.get("phone"),
                        (String) claims.get("nickName"),
                        (List<String>) claims.get("roleNames"),
                        active
                );
                member.setEmail((String) claims.get("email"));
                // 필요한 다른 정보들 설정...

                Authentication auth = new UsernamePasswordAuthenticationToken(
                        member, "", member.getAuthorities()
                );
                accessor.setUser(auth);
            }
        }
        return message;
    }

}
//package com.himedia.luckydokiapi.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.messaging.Message;
//import org.springframework.messaging.MessageChannel;
//import org.springframework.messaging.simp.config.ChannelRegistration;
//import org.springframework.messaging.simp.config.MessageBrokerRegistry;
//import org.springframework.messaging.simp.stomp.StompCommand;
//import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
//import org.springframework.messaging.support.ChannelInterceptor;
//import org.springframework.messaging.support.MessageHeaderAccessor;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
//import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
//import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
//
//@Configuration
//@EnableWebSocketMessageBroker
//public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
//    @Override
//    public void configureMessageBroker(MessageBrokerRegistry config) {
//        config.enableSimpleBroker("/topic"); //구독 주소
//        config.setApplicationDestinationPrefixes("/app"); // 메시지 발행 경로
//    }
//
//    @Override
//    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        registry.addEndpoint("/ws-stomp")
//                .setAllowedOrigins("*")
//                .withSockJS();  //필수 설정값
//    }
//
//    @Override
//    public void configureClientInboundChannel(ChannelRegistration config) {
//        config.interceptors(new ChannelInterceptor() {
//            @Override
//            public Message<?> preSend(Message<?> message, MessageChannel channel) {
//                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
//                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
//                    // Security Context에서 인증 정보 가져오기
//                    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//                    accessor.setUser(auth);
//                }
//                return message;
//            }
//        });
//
//    }
//}

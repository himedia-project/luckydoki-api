package com.himedia.luckydokiapi.domain.notification.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

import com.himedia.luckydokiapi.domain.notification.enums.NotificationType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@Transactional
public class FCMService {

    public void sendNotification(Long shopId, String targetEmail, String token, String title, String body, NotificationType type) {
        log.info("sendNotification shopId: {}, token: {}, title: {}, body: {}, type: {}", shopId, token, title, body, type);
        Message message = Message.builder()
                .putData("type", type.name())
                .putData("shopId", shopId == null ? "" : shopId.toString())
                .putData("targetEmail", targetEmail) // 데이터에 추가
                .putData("title", title)  // 데이터에도 추가
                .putData("body", body)    // 데이터에도 추가
                .putData("timestamp", LocalDateTime.now().toString()) // 데이터에도 추가
                .setToken(token)
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("FCM Token: {}", token);
            log.info("Message: {}", message);
            log.info("Successfully sent notification: {}", response);
        } catch (Exception e) {
            log.error("Failed to send notification", e);
        }
    }
}
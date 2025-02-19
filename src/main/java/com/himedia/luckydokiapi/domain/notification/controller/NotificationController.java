package com.himedia.luckydokiapi.domain.notification.controller;

import com.himedia.luckydokiapi.domain.notification.service.NotificationService;
import com.himedia.luckydokiapi.security.MemberDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/list")
    public ResponseEntity<?> getNotificationList(@AuthenticationPrincipal MemberDTO memberDTO) {
        log.info("getNotificationList memberDTO : {}", memberDTO);
        return ResponseEntity.ok(notificationService.list(memberDTO.getEmail()));
    }

}

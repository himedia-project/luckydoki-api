package com.himedia.luckydokiapi.domain.email.controller;

import com.himedia.luckydokiapi.domain.email.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(
            @RequestParam String to,
            @RequestParam String subject,
            @RequestParam String message) {

        emailService.sendSimpleEmail(to, subject, message);
        return ResponseEntity.ok(" 이메일이 성공적으로 전송되었습니다!");
    }
}

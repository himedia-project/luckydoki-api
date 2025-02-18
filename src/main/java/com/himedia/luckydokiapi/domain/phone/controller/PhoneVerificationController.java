package com.himedia.luckydokiapi.domain.phone.controller;


import com.himedia.luckydokiapi.domain.phone.dto.PhoneVerificationRequestDTO;
import com.himedia.luckydokiapi.domain.phone.service.PhoneVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/phone")
@RequiredArgsConstructor
public class PhoneVerificationController {

    private final PhoneVerificationService verificationService;

    @PostMapping("/send")
    public ResponseEntity<String> sendVerification(@RequestBody PhoneVerificationRequestDTO request) {
        verificationService.sendVerificationCode(request.getPhone());
        return ResponseEntity.ok("인증 코드가 전송되었습니다.");
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyCode(@RequestBody PhoneVerificationRequestDTO request) {
        boolean isValid = verificationService.verifyCode(request.getPhone(), request.getCode());
        if (isValid) {
            return ResponseEntity.ok("인증 성공");
        } else {
            return ResponseEntity.badRequest().body("인증 실패");
        }
    }
}

package com.himedia.luckydokiapi.domain.phone.service;


import com.himedia.luckydokiapi.util.SMSUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PhoneVerificationService {

    private final SMSUtil smsUtil;
    private final Map<String, String> verificationStorage = new HashMap<>();

    public void sendVerificationCode(String phoneNumber) {
        String code = smsUtil.sendVerificationCode(phoneNumber);
        verificationStorage.put(phoneNumber, code);
        log.info("Verification code stored for {}", phoneNumber);
    }

    public boolean verifyCode(String phoneNumber, String code) {
        return code.equals(verificationStorage.get(phoneNumber));
    }
}

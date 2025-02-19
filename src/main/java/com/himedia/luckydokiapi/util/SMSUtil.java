package com.himedia.luckydokiapi.util;

import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Random;

@Component
public class SMSUtil {

    private final DefaultMessageService messageService;

    @Value("${sms.api-key}")
    private String apiKey;

    @Value("${sms.api-secret}")
    private String apiSecret;

    @Value("${sms.from}")
    private String fromNumber;

    public SMSUtil(
            @Value("${sms.api-key}") String apiKey,
            @Value("${sms.api-secret}") String apiSecret) {
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");
    }

    public String sendVerificationCode(String phoneNumber) {
        String code = generateCode();

        Message message = new Message();
        message.setFrom(fromNumber);
        message.setTo(phoneNumber);
        message.setText("[인증번호] " + code + " (5분 이내 입력)");

        messageService.sendOne(new SingleMessageSendingRequest(message));

        return code; // 인증 코드 반환
    }

    private String generateCode() {
        Random random = new Random();
        return String.valueOf(100000 + random.nextInt(900000)); // 6자리 인증 코드 생성
    }
}

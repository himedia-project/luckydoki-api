package com.himedia.luckydokiapi.domain.email.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendSimpleEmail(String toEmail, String subject, String messageText) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(messageText, true);

            mailSender.send(message);
            log.info(" 이메일이 {}에게 성공적으로 전송됨", toEmail);
        } catch (MessagingException e) {
            log.error(" 이메일 전송 실패", e);
        }

    }

    public void sendPaymentConfirmation(String toEmail, String orderId, String amount) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(" 결제 완료 안내 - 주문번호: " + orderId);
            helper.setText(
                    "<h2>결제가 성공적으로 완료되었습니다!</h2>" +
                            "<p>주문번호: <strong>" + orderId + "</strong></p>" +
                            "<p>결제 금액: <strong>" + amount + "원</strong></p>" +
                            "<p>이용해주셔서 감사합니다.</p>",
                    true // HTML 활성화
            );

            mailSender.send(message);
            log.info(" 결제 완료 이메일이 {}에게 성공적으로 전송됨", toEmail);
        } catch (MessagingException e) {
            log.error(" 결제 완료 이메일 전송 실패", e);
        }
    }
}

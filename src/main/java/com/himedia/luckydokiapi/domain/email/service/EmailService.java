package com.himedia.luckydokiapi.domain.email.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendSimpleEmail(String toEmail, String subject, String messageText) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(messageText, false);

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


            Context context = new Context();
            context.setVariable("orderId", orderId);
            context.setVariable("amount", amount);

            String htmlContent = templateEngine.process("email/order-confirmation", context);

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("🎉 결제 완료 안내 - 주문번호: " + orderId);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("결제 완료 이메일이 {}에게 성공적으로 전송됨", toEmail);
        } catch (MessagingException e) {
            log.error("결제 완료 이메일 전송 실패", e);
        }
    }

}



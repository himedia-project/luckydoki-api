package com.himedia.luckydokiapi.domain.notification.service;

import com.himedia.luckydokiapi.domain.member.entity.Member;
import com.himedia.luckydokiapi.domain.member.repository.MemberRepository;
import com.himedia.luckydokiapi.domain.notification.dto.NotificationResponseDTO;
import com.himedia.luckydokiapi.domain.notification.entity.Notification;
import com.himedia.luckydokiapi.domain.notification.enums.NotificationType;
import com.himedia.luckydokiapi.domain.notification.repository.NotificationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final FCMService fcmService;
    private final MemberRepository memberRepository;

    public void sendSellerApprovalNotification(String targetEmail) {
        log.info("sendSellerApprovalNotification: target targetEmail {}", targetEmail);
        Member member = memberRepository.getWithRoles(targetEmail)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 회원이 없습니다. targetEmail: " + targetEmail));
        String title = "셀러 승인";
        String body = "셀러 승인이 완료되었습니다.";

        // DB에 알림 저장
        Notification notification = Notification.of(
                NotificationType.SELLER_APPROVAL,
                member,
                title,
                body,
                member.getFcmToken()
        );
        notificationRepository.save(notification);


        // FCM 알림 전송
        if (member.getFcmToken() != null) {
            log.info("sendSellerApprovalNotification: FCM 알림 전송됨!");
            fcmService.sendNotification(
                    member.getFcmToken(),
                    title,
                    body,
                    NotificationType.SELLER_APPROVAL
            );
        }
        log.info("sendSellerApprovalNotification 서비스 end");
    }

    public List<NotificationResponseDTO> list(String targetEmail) {
        Member member = memberRepository.getWithRoles(targetEmail)
                .orElseThrow(() -> new EntityNotFoundException("해당 이메일을 가진 회원이 없습니다. targetEmail: " + targetEmail));

        return notificationRepository.findByEmail(member.getEmail()).stream()
                .map(NotificationResponseDTO::from).toList();
    }
}
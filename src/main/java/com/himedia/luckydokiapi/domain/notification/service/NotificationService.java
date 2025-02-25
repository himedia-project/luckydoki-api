package com.himedia.luckydokiapi.domain.notification.service;

import com.himedia.luckydokiapi.domain.chat.dto.ChatMessageDTO;
import com.himedia.luckydokiapi.domain.member.entity.Member;
import com.himedia.luckydokiapi.domain.member.repository.MemberRepository;
import com.himedia.luckydokiapi.domain.notification.dto.NotificationResponseDTO;
import com.himedia.luckydokiapi.domain.notification.entity.Notification;
import com.himedia.luckydokiapi.domain.notification.enums.NotificationType;
import com.himedia.luckydokiapi.domain.notification.repository.NotificationRepository;
import com.himedia.luckydokiapi.domain.product.entity.Product;
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


    /**
     * 쿠폰 발급 알림전송
     *
     * @param member 회원
     */
    public void sendCouponToMember(Member member, String title, String body, NotificationType type) {
        log.info("sendCouponToMember notification: member email {}", member.getEmail());
        Notification notification = Notification.of(
                type,
                member,
                title,
                body,
                member.getFcmToken()
        );
        notificationRepository.save(notification);
        this.fcmNotificationSender(member, title, body, type);
    };


    /**
     * 회원가입 Welcome 쿠폰 발급 알림전송
     */
    public void sendWelcomeCouponToMember(Member member) {
        log.info("sendWelcomeCouponToMember notification: member email {}", member.getEmail());
        String title = "회원가입 축하 🎉🎉";
        String body = "회원가입 축하 쿠폰이 발급되었습니다.";
        sendCouponToMember(member, title, body, NotificationType.WELCOME);
    }

    /**
     * 셀러 승인 알림 전송
     *
     * @param targetEmail 알림을 받을 회원 이메일
     */
    public void sendSellerApproval(String targetEmail) {
        log.info("sendSellerApproval notification: target targetEmail {}", targetEmail);
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
        this.fcmNotificationSender(member, title, body, NotificationType.SELLER_APPROVAL);
    }


    /**
     * 새 메세지 알림 전송
     *
     * @param targetEmail 알림을 받을 회원 이메일
     */

    public void sendChattingMessage(String targetEmail, ChatMessageDTO chatMessageDTO) {
        log.info("sendChattingMessage notification: target targetEmail {}", targetEmail);
        Member member = memberRepository.findByNickName(targetEmail)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 회원이 없습니다. targetEmail: " + targetEmail));
        String title = chatMessageDTO.getEmail() + "님 에게 새 메세지가 도착하였습니다!";
        String body = chatMessageDTO.getMessage();

        Notification notification = Notification.of(
                NotificationType.NEW_MESSAGE,
                member,
                title,
                body,
                member.getFcmToken()
        );
        notificationRepository.save(notification);
        this.fcmNotificationSender(member, title, body, NotificationType.NEW_MESSAGE);
    }




    /**
     * 상품 승인 알림 전송
     * @param idList 알림을 받을 상품 ID 목록
     */
    public void sendProductApprovalNotification(List<Product> idList) {
        for (Product product : idList) {
            Member member = product.getShop().getMember();
            String title = "상품 승인";
            String body = "상품 승인이 완료되었습니다.";
            Notification notification = Notification.of(
                    NotificationType.PRODUCT_APPROVAL,
                    member,
                    title,
                    body,
                    member.getFcmToken()
            );
            notificationRepository.save(notification);
            this.fcmNotificationSender(member, title, body, NotificationType.PRODUCT_APPROVAL);
        }
    }


    /**
     * 알림 목록 조회
     * @param targetEmail 알림을 받을 회원 이메일
     * @return 알림 목록
     */
    public List<NotificationResponseDTO> list(String targetEmail) {
        Member member = memberRepository.getWithRoles(targetEmail)
                .orElseThrow(() -> new EntityNotFoundException("해당 이메일을 가진 회원이 없습니다. targetEmail: " + targetEmail));

        return notificationRepository.findByEmail(member.getEmail()).stream()
                .map(NotificationResponseDTO::from).toList();
    }


    /**
     * FCM 알림 전송
     *
     * @param member 회원
     * @param title  제목
     * @param body   내용
     */
    private void fcmNotificationSender(Member member, String title, String body, NotificationType type) {
        // FCM 알림 전송
        if (member.getFcmToken() != null) {
            log.info("sendSellerApprovalNotification: FCM 알림 전송됨! member.getFcmToken(): {}", member.getFcmToken());
            fcmService.sendNotification(
                    member.getFcmToken(),
                    title,
                    body,
                    type
            );
        }
        log.info("sendSellerApprovalNotification 서비스 end");
    }

}
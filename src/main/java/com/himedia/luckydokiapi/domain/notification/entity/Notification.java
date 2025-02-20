package com.himedia.luckydokiapi.domain.notification.entity;

import com.himedia.luckydokiapi.domain.member.entity.Member;
import com.himedia.luckydokiapi.domain.notification.enums.NotificationType;
import com.himedia.luckydokiapi.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
@Table(name = "notification")
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_email", referencedColumnName = "email")
    private Member member;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String body;

    private String fcmToken;

    private boolean isRead;

    public static Notification of(NotificationType type,
                                  Member target,
                                  String title,
                                  String body,
                                  String fcmToken) {
        return Notification.builder()
                .type(type)
                .member(target)
                .title(title)
                .body(body)
                .fcmToken(fcmToken)
                .isRead(false)
                .build();
    }

    public void markAsRead() {
        this.isRead = true;
    }
}

package com.himedia.luckydokiapi.domain.notification.repository;

import com.himedia.luckydokiapi.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("select n from Notification n where n.member.email = :targetEmail")
    List<Notification> findByEmail(@Param("targetEmail") String targetEmail);
}

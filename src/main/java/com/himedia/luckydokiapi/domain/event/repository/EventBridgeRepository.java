package com.himedia.luckydokiapi.domain.event.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.himedia.luckydokiapi.domain.event.entity.EventBridge;
import com.himedia.luckydokiapi.domain.event.repository.querydsl.EventBridgeRepositoryCustom;

public interface EventBridgeRepository extends JpaRepository<EventBridge, Long>, EventBridgeRepositoryCustom {
}
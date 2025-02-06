package com.himedia.luckydokiapi.domain.event.repository.querydsl;
import java.util.List;
import com.himedia.luckydokiapi.domain.event.entity.EventBridge;

public interface EventBridgeRepositoryCustom {
	List<EventBridge> findByEventId(Long eventId);
	void deleteByEventIdAndProductId(Long eventId, Long productId);
}
package com.himedia.luckydokiapi.domain.event.service;

import com.himedia.luckydokiapi.domain.event.dto.EventBridgeDto;
import java.util.List;

public interface EventBridgeService {
	EventBridgeDto addProductToEvent(Long eventId, Long productId);
	void removeProductFromEvent(Long bridgeId, Long productId);
	List<EventBridgeDto> getProductsByEventId(Long eventId);
}

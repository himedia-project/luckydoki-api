package com.himedia.luckydokiapi.domain.event.repository.querydsl;

import com.himedia.luckydokiapi.domain.event.entity.EventBridge;
import com.himedia.luckydokiapi.domain.event.entity.QEventBridge;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class EventBridgeRepositoryImpl implements EventBridgeRepositoryCustom {
	private final JPAQueryFactory queryFactory;
	
	@Override
	public List<EventBridge> findByEventId(Long eventId) {
		QEventBridge eventBridge = QEventBridge.eventBridge;
		
		return queryFactory
				.selectFrom(eventBridge)
				.where(eventBridge.event.id.eq(eventId))
				.fetch();
	}
	
	@Override
	public void deleteByEventIdAndProductId(Long eventId, Long productId) {
		QEventBridge eventBridge = QEventBridge.eventBridge;
		
		queryFactory
				.delete(eventBridge)
				.where(eventBridge.event.id.eq(eventId)
						.and(eventBridge.product.id.eq(productId)))
				.execute();
	}
}

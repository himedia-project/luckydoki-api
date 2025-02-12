package com.himedia.luckydokiapi.domain.event.repository.querydsl;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static com.himedia.luckydokiapi.domain.event.entity.QEvent.event;

import com.himedia.luckydokiapi.domain.event.entity.Event;

@Repository
@RequiredArgsConstructor
public class EventRepositoryImpl implements EventRepositoryCustom {
	private final JPAQueryFactory queryFactory;
	
	@Override
	public List<Event> findActiveEvents(LocalDateTime now) {
		// KST 시간으로 변경
		LocalDateTime kstNow = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
		
		return queryFactory
				.selectFrom(event)
				.where(event.startAt.loe(kstNow)
						.and(event.endAt.goe(kstNow)))
				.fetch();
	}
}

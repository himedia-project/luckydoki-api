package com.himedia.luckydokiapi.domain.event.repository.querydsl;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.himedia.luckydokiapi.domain.event.entity.QEvent.event;
import com.himedia.luckydokiapi.domain.event.entity.Event;

@Repository
@RequiredArgsConstructor
public class EventRepositoryImpl implements EventRepositoryCustom {
	private final JPAQueryFactory queryFactory;
	
	@Override
	public List<Event> findActiveEvents(LocalDateTime now) {
		return queryFactory
				.selectFrom(event)
				.where(event.startAt.before(now)
						.and(event.endAt.after(now)))
				.fetch();
	}
}

package com.himedia.luckydokiapi.domain.event.repository.querydsl;

import com.himedia.luckydokiapi.domain.event.dto.EventSearchDto;
import com.himedia.luckydokiapi.domain.event.entity.Event;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static com.himedia.luckydokiapi.domain.event.entity.QEvent.event;
import static com.himedia.luckydokiapi.domain.event.entity.QEventBridge.eventBridge;

@Repository
@RequiredArgsConstructor
public class EventRepositoryImpl implements EventRepositoryCustom {

	private final JPAQueryFactory queryFactory;


	@Override
	public Page<Event> findListBy(EventSearchDto requestDto) {

		Pageable pageable = PageRequest.of(
				requestDto.getPage() - 1,
				requestDto.getSize(),
				"asc".equals(requestDto.getSort()) ?
						Sort.by("id").ascending() : Sort.by("id").descending()
		);

		List<Event> list = queryFactory
				.selectFrom(event)
				.leftJoin(event.eventBridgeList, eventBridge)
				.where(
						containsSearchKeyword(requestDto.getSearchKeyword())
				)
				.orderBy(event.id.desc())
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize())
				.fetch();

		JPAQuery<Event> countQuery = queryFactory
				.selectFrom(event)
				.leftJoin(event.eventBridgeList, eventBridge)
				.where(
						containsSearchKeyword(requestDto.getSearchKeyword())
				);

		return PageableExecutionUtils.getPage(list, pageable, countQuery::fetchCount);
	}


	@Override
	public List<Event> findActiveEvents(LocalDateTime now) {
		// KST 시간으로 변경
//		LocalDateTime kstNow = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
		LocalDate kstNow = LocalDate.now(ZoneId.of("Asia/Seoul"));
		
		return queryFactory
				.selectFrom(event)
				.where(
						event.startAt.loe(kstNow),
						event.endAt.goe(kstNow)
				)
				.fetch();
	}


	private BooleanExpression containsSearchKeyword(String searchKeyword) {
		if (searchKeyword == null) {
			return null;
		}
		return event.title.contains(searchKeyword)
				.or(event.content.contains(searchKeyword));
	}

}

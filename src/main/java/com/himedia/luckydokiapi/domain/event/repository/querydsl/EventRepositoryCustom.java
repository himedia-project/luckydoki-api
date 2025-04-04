package com.himedia.luckydokiapi.domain.event.repository.querydsl;
import java.time.LocalDateTime;
import java.util.List;

import com.himedia.luckydokiapi.domain.event.dto.EventSearchDto;
import com.himedia.luckydokiapi.domain.event.entity.Event;
import org.springframework.data.domain.Page;

public interface EventRepositoryCustom {
	List<Event> findActiveEvents(LocalDateTime now);

    Page<Event> findListBy(EventSearchDto requestDto);
}

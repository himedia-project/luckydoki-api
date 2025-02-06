package com.himedia.luckydokiapi.domain.event.repository.querydsl;
import java.time.LocalDateTime;
import java.util.List;
import com.himedia.luckydokiapi.domain.event.entity.Event;

public interface EventRepositoryCustom {
	List<Event> findActiveEvents(LocalDateTime now);
}

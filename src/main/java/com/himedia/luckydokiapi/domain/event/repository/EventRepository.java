package com.himedia.luckydokiapi.domain.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.himedia.luckydokiapi.domain.event.entity.Event;
import com.himedia.luckydokiapi.domain.event.repository.querydsl.EventRepositoryCustom;

public interface EventRepository extends JpaRepository<Event, Long>
        , EventRepositoryCustom {
}

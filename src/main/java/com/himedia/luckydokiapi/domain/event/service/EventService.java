package com.himedia.luckydokiapi.domain.event.service;

import com.himedia.luckydokiapi.domain.event.dto.EventDto;

import java.util.List;

public interface EventService {
	List<EventDto> getAllEvents();
	List<EventDto> getActiveEvents();
	EventDto getEventById(Long id);
	EventDto createEvent(EventDto eventDto);
	EventDto updateEvent(Long id, EventDto eventDto);
	void deleteEvent(Long id);
}

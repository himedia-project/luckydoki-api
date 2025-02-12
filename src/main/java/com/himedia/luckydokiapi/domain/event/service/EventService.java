package com.himedia.luckydokiapi.domain.event.service;

import com.himedia.luckydokiapi.domain.event.dto.EventDto;
import com.himedia.luckydokiapi.domain.event.dto.EventRequestDto;

import java.util.List;

public interface EventService {
	List<EventDto> getAllEvents();
	List<EventDto> getActiveEvents();
	EventDto getEventById(Long id);
	EventDto createEvent(EventRequestDto eventRequestDto);
	EventDto updateEvent(Long id, EventRequestDto eventRequestDto);
	void deleteEvent(Long id);
}

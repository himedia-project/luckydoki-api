package com.himedia.luckydokiapi.domain.event.service;

import com.himedia.luckydokiapi.domain.event.dto.EventDto;
import com.himedia.luckydokiapi.domain.event.dto.EventRequestDto;
import com.himedia.luckydokiapi.domain.event.dto.EventSearchDto;
import com.himedia.luckydokiapi.dto.PageResponseDTO;

import java.util.List;

public interface EventService {
	List<EventDto> getAllEvents();
	List<EventDto> getActiveEvents();
	EventDto getEventById(Long id);
	Long createEvent(EventRequestDto eventRequestDto);
	EventDto updateEvent(Long id, EventRequestDto eventRequestDto);
	void deleteEvent(Long id);

	PageResponseDTO<EventDto> getEvents(EventSearchDto requestDto);
}

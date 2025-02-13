package com.himedia.luckydokiapi.domain.event.service;

import com.himedia.luckydokiapi.domain.event.dto.EventDto;
import com.himedia.luckydokiapi.domain.event.dto.EventRequestDto;
import com.himedia.luckydokiapi.domain.event.dto.EventSearchDto;
import com.himedia.luckydokiapi.domain.event.entity.Event;
import com.himedia.luckydokiapi.domain.event.repository.EventRepository;
import com.himedia.luckydokiapi.dto.PageResponseDTO;
import com.himedia.luckydokiapi.exception.EventNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
	private final EventRepository eventRepository;
	
	@Override
	public List<EventDto> getAllEvents() {
		return eventRepository.findAll()
				.stream()
				.map(this::convertToDto)
				.collect(Collectors.toList());
	}
	
	@Override
	public List<EventDto> getActiveEvents() {
		List<Event> activeEvents = eventRepository.findActiveEvents(LocalDateTime.now());
		return activeEvents.stream()
				.map(this::convertToDto)
				.collect(Collectors.toList());
	}
	
	@Override
	public EventDto getEventById(Long id) {
		Event event = eventRepository.findById(id)
				.orElseThrow(() -> new EventNotFoundException(id));
		return convertToDto(event);
	}
	
	@Transactional
	@Override
	public EventDto createEvent(EventRequestDto eventRequestDto) {
		eventRequestDto.sanitize();
		Event event = Event.builder()
				.title(eventRequestDto.getTitle())
				.content(eventRequestDto.getContent())
				.image(eventRequestDto.getImage())
				.startAt(eventRequestDto.getStartAt())
				.endAt(eventRequestDto.getEndAt())
				.build();
		
		Event savedEvent = eventRepository.save(event);
		return convertToDto(savedEvent);
	}
	
	@Transactional
	@Override
	public EventDto updateEvent(Long id, EventRequestDto eventRequestDto) {
		eventRequestDto.sanitize();
		Event existingEvent = eventRepository.findById(id)
				.orElseThrow(() -> new EventNotFoundException(id));
		
		Event updatedEvent = Event.builder()
				.id(existingEvent.getId())
				.title(eventRequestDto.getTitle())
				.content(eventRequestDto.getContent())
				.image(eventRequestDto.getImage())
				.startAt(eventRequestDto.getStartAt())
				.endAt(eventRequestDto.getEndAt())
				.createdAt(existingEvent.getCreatedAt())
				.modifiedAt(LocalDateTime.now())
				.build();
		
		Event savedEvent = eventRepository.save(updatedEvent);
		return convertToDto(savedEvent);
	}
	
	@Transactional
	@Override
	public void deleteEvent(Long id) {
		if (!eventRepository.existsById(id)) {
			throw new EventNotFoundException(id);
		}
		eventRepository.deleteById(id);
	}

    @Override
    public PageResponseDTO<EventDto> getEvents(EventSearchDto requestDto) {
        return null;
    }

    private EventDto convertToDto(Event event) {
		return EventDto.builder()
				.id(event.getId())
				.title(event.getTitle())
				.content(event.getContent())
				.image(event.getImage())
				.startAt(event.getStartAt())
				.endAt(event.getEndAt())
				.build();
	}
}

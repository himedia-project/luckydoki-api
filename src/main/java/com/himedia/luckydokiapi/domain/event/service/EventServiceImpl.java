//package com.himedia.luckydokiapi.domain.event.service;
//
//import com.himedia.luckydokiapi.domain.event.dto.EventDto;
//import com.himedia.luckydokiapi.domain.event.entity.Event;
//import com.himedia.luckydokiapi.exception.EventNotFoundException;
//import com.himedia.luckydokiapi.domain.event.repository.EventRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//public class EventServiceImpl implements EventService {
//	private final EventRepository eventRepository;
//
//
//	@Override
//	public List<EventDto> getAllEvents() {
//		return eventRepository.findAll()
//				.stream()
//				.map(this::convertToDto)
//				.collect(Collectors.toList());
//	}
//
//	@Override
//	public List<EventDto> getActiveEvents() {
//		List<Event> activeEvents = eventRepository.findActiveEvents(LocalDateTime.now());
//		return activeEvents.stream()
//				.map(this::convertToDto)
//				.collect(Collectors.toList());
//	}
//
//	@Override
//	public EventDto getEventById(Long id) {
//		Event event = eventRepository.findById(id)
//				.orElseThrow(() -> new EventNotFoundException(id));
//		return convertToDto(event);
//	}
//
////	@Transactional
////	@Override
////	public EventDto createEvent(EventDto eventDto) {
////		Event event = new Event();
////		event.setTitle(eventDto.getTitle());
////		event.setContent(eventDto.getContent());
////		event.setImage(eventDto.getImage());
////		event.setStartAt(eventDto.getStartAt());
////		event.setEndAt(eventDto.getEndAt());
////
////		Event savedEvent = eventRepository.save(event);
////		return convertToDto(savedEvent);
////	}
////
////	@Transactional
////	@Override
////	public EventDto updateEvent(Long id, EventDto eventDto) {
////		Event event = eventRepository.findById(id)
////				.orElseThrow(() -> new EventNotFoundException(id));
////
////		event.setTitle(eventDto.getTitle());
////		event.setContent(eventDto.getContent());
////		event.setImage(eventDto.getImage());
////		event.setStartAt(eventDto.getStartAt());
////		event.setEndAt(eventDto.getEndAt());
////
////		Event updatedEvent = eventRepository.save(event); // 명시적으로 저장
////		return convertToDto(updatedEvent);
////	}
//
//
//	@Transactional
//	@Override
//	public void deleteEvent(Long id) {
//		if (!eventRepository.existsById(id)) {
//			throw new EventNotFoundException(id);
//		}
//		eventRepository.deleteById(id);
//	}
//
//	private EventDto convertToDto(Event event) {
//		return EventDto.builder()
//				.id(event.getId())
//				.title(event.getTitle())
//				.content(event.getContent())
//				.image(event.getImage())
//				.startAt(event.getStartAt())
//				.endAt(event.getEndAt())
//				.build();
//	}
//}

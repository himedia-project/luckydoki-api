package com.himedia.luckydokiapi.domain.event.service;

import com.himedia.luckydokiapi.domain.event.dto.EventDto;
import com.himedia.luckydokiapi.domain.event.dto.EventRequestDto;
import com.himedia.luckydokiapi.domain.event.dto.EventSearchDto;
import com.himedia.luckydokiapi.domain.event.entity.Event;
import com.himedia.luckydokiapi.domain.event.repository.EventBridgeRepository;
import com.himedia.luckydokiapi.domain.event.repository.EventRepository;
import com.himedia.luckydokiapi.dto.PageResponseDTO;
import com.himedia.luckydokiapi.exception.EventNotFoundException;
import com.himedia.luckydokiapi.util.file.CustomFileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {


	private final EventRepository eventRepository;
	private final EventBridgeService eventBridgeService;

	private final CustomFileUtil fileUtil;
	private final EventBridgeRepository eventBridgeRepository;

	@Transactional(readOnly = true)
	@Override
	public PageResponseDTO<EventDto> getEvents(EventSearchDto requestDto) {

		Page<Event> result = eventRepository.findListBy(requestDto);

		return PageResponseDTO.<EventDto>withAll()
				.dtoList(result.getContent().stream().map(this::convertToDto).toList())
				.totalCount(result.getTotalElements())
				.pageRequestDTO(requestDto)
				.build();
	}


	@Transactional(readOnly = true)
	@Override
	public List<EventDto> getAllEvents() {
		return eventRepository.findAll()
				.stream()
				.map(this::convertToDto)
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	@Override
	public List<EventDto> getActiveEvents() {
		List<Event> activeEvents = eventRepository.findActiveEvents(LocalDateTime.now());
		return activeEvents.stream()
				.map(this::convertToDto)
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	@Override
	public EventDto getEventById(Long id) {
		Event event = getEvent(id);
		return convertToDto(event);
	}


	@Override
	public Long createEvent(EventRequestDto requestDto) {
		requestDto.sanitize();

		// 이미지 업로드
		if (requestDto.getFile() != null) {
			requestDto.setImage(fileUtil.uploadS3File(requestDto.getFile()));
		}

		Event event = Event.builder()
				.title(requestDto.getTitle())
				.content(requestDto.getContent())
				.startAt(requestDto.getStartAt())
				.endAt(requestDto.getEndAt())
				.image(requestDto.getImage())
				.build();

		Event savedEvent = eventRepository.save(event);

		// 이벤트 생성 시 상품 추가
		if(requestDto.getProductIds() != null) {
			requestDto.getProductIds().forEach(productId -> {
				eventBridgeService.addProductToEvent(savedEvent.getId(), productId);
			});
		}

		return savedEvent.getId();
	}


	@Override
	public EventDto updateEvent(Long id, EventRequestDto eventRequestDto) {
		eventRequestDto.sanitize();
		Event existingEvent = getEvent(id);

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


	@Override
	public void deleteEvent(Long eventId) {
		getEvent(eventId);
		eventRepository.deleteById(eventId);
	}


	/**
	 * 이벤트 조회
	 * @param id 이벤트 ID
	 * @return 이벤트
	 */
	private Event getEvent(Long id) {
		return eventRepository.findById(id)
				.orElseThrow(() -> new EventNotFoundException(id));
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

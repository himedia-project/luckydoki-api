package com.himedia.luckydokiapi.domain.event.service;

import com.himedia.luckydokiapi.domain.event.dto.EventBridgeDto;
import com.himedia.luckydokiapi.domain.event.entity.Event;
import com.himedia.luckydokiapi.domain.event.entity.EventBridge;
import com.himedia.luckydokiapi.exception.EventNotFoundException;
import com.himedia.luckydokiapi.domain.event.repository.EventBridgeRepository;
import com.himedia.luckydokiapi.domain.event.repository.EventRepository;
import com.himedia.luckydokiapi.domain.product.entity.Product;
import com.himedia.luckydokiapi.domain.product.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventBridgeServiceImpl implements EventBridgeService {
	private final EventBridgeRepository eventBridgeRepository;
	private final EventRepository eventRepository;
	private final ProductRepository productRepository;
	
	@Transactional
	@Override
	public EventBridgeDto addProductToEvent(Long eventId, Long productId) {
		Event event = eventRepository.findById(eventId)
				.orElseThrow(() -> new EventNotFoundException(eventId));
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new EntityNotFoundException(String.valueOf(productId)));
		
		EventBridge eventBridge = new EventBridge();
		eventBridge.setEvent(event);
		eventBridge.setProduct(product);
		
		EventBridge savedBridge = eventBridgeRepository.save(eventBridge);
		return convertToDto(savedBridge);
	}
	
	@Transactional
	@Override
	public void removeProductFromEvent(Long eventId, Long productId) {
		eventBridgeRepository.deleteByEventIdAndProductId(eventId, productId);
	}
	
	
	
	@Override
	public List<EventBridgeDto> getProductsByEventId(Long eventId) {
		List<EventBridge> eventBridges = eventBridgeRepository.findByEventId(eventId);
		return eventBridges.stream()
				.map(this::convertToDto)
				.collect(Collectors.toList());
	}
	
	private EventBridgeDto convertToDto(EventBridge eventBridge) {
		return EventBridgeDto.builder()
				.id(eventBridge.getId())
				.eventId(eventBridge.getEvent().getId())
				.productId(eventBridge.getProduct().getId())
				.build();
	}
}

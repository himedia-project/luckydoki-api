package com.himedia.luckydokiapi.domain.event.controller;

import com.himedia.luckydokiapi.domain.event.dto.EventBridgeDto;
import com.himedia.luckydokiapi.domain.event.dto.EventDto;
import com.himedia.luckydokiapi.domain.event.dto.EventRequestDto;
import com.himedia.luckydokiapi.domain.event.service.EventBridgeService;
import com.himedia.luckydokiapi.domain.event.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/admin/event")
@RequiredArgsConstructor
public class AdminEventController {
	private final EventService eventService;
	private final EventBridgeService eventBridgeService;
	
	// ✅ 이벤트 생성 (관리자 전용)
	@PostMapping
	public ResponseEntity<EventDto> createEvent(@RequestBody EventRequestDto eventRequestDto) {
		log.info("createEvent: {}", eventRequestDto);
		return ResponseEntity.ok(eventService.createEvent(eventRequestDto));
	}
	
	// ✅ 이벤트 수정 (관리자 전용)
	@PutMapping("/{id}")
	public ResponseEntity<EventDto> updateEvent(@PathVariable Long id, @RequestBody EventRequestDto eventRequestDto) {
		log.info("updateEvent: {}, {}", id, eventRequestDto);
		return ResponseEntity.ok(eventService.updateEvent(id, eventRequestDto));
	}
	
	// ✅ 이벤트 삭제 (관리자 전용)
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
		log.info("deleteEvent: {}", id);
		eventService.deleteEvent(id);
		return ResponseEntity.noContent().build();
	}
	
	// ✅ 이벤트에 상품 추가
	@PostMapping("/{eventId}/{productId}")
	public ResponseEntity<EventBridgeDto> addProductToEvent(
			@PathVariable Long eventId,
			@PathVariable Long productId) {
		log.info("addProductToEvent: {}, {}", eventId, productId);
		return ResponseEntity.ok(eventBridgeService.addProductToEvent(eventId, productId));
	}
	
	// ✅ 이벤트에서 특정 상품 제거
	@DeleteMapping("/{eventId}/{productId}")
	public ResponseEntity<Void> removeProductFromEvent(
			@PathVariable Long eventId,
			@PathVariable Long productId) {
		log.info("removeProductFromEvent: {}, {}", eventId, productId);
		eventBridgeService.removeProductFromEvent(eventId, productId);
		return ResponseEntity.noContent().build();
	}
}

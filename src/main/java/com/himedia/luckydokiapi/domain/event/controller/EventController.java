package com.himedia.luckydokiapi.domain.event.controller;

import com.himedia.luckydokiapi.domain.event.dto.EventBridgeDto;
import com.himedia.luckydokiapi.domain.event.dto.EventDto;
import com.himedia.luckydokiapi.domain.event.service.EventBridgeService;
import com.himedia.luckydokiapi.domain.event.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/event")
@RequiredArgsConstructor
@Tag(name = "event-api", description = " 이벤트와 이벤트 상품을 조회하는 api 입니다")
public class EventController {
	private final EventService eventService;
	private final EventBridgeService eventBridgeService;


	@GetMapping("/list")
	public ResponseEntity<List<EventDto>> getAllEvents() {
		return ResponseEntity.ok(eventService.getAllEvents());
	}

	// 활성화된 이벤트만 조회
	@Operation(summary = "활성화 이벤트 조회 ", description = "활성화 된 상태인 이벤트를 조회 합니다")
	@GetMapping("/active")
	public ResponseEntity<List<EventDto>> getActiveEvents() {
		return ResponseEntity.ok(eventService.getActiveEvents());
	}

	// 특정 이벤트 조회

	@GetMapping("/{id}")
	public ResponseEntity<EventDto> getEventById(@PathVariable Long id) {
		return ResponseEntity.ok(eventService.getEventById(id));
	}

	//이벤트에 속한 모든 상품 조회
	@Operation(summary = "이벤트 상품 조회", description = "이벤트 아이디로 해당하는 상품들을 보여줍니다")
	@GetMapping("/{eventId}/product/list")
	public ResponseEntity<List<EventBridgeDto>> getProductsByEventId(@PathVariable Long eventId) {
		return ResponseEntity.ok(eventBridgeService.getProductsByEventId(eventId));
	}
}

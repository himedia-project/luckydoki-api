//package com.himedia.luckydokiapi.domain.event.controller;
//
//import com.himedia.luckydokiapi.domain.event.dto.EventBridgeDto;
//import com.himedia.luckydokiapi.domain.event.dto.EventDto;
//import com.himedia.luckydokiapi.domain.event.service.EventBridgeService;
//import com.himedia.luckydokiapi.domain.event.service.EventService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/event")
//@RequiredArgsConstructor
//public class EventController {
//	private final EventService eventService;
//	private final EventBridgeService eventBridgeService;
//
//	// 모든 이벤트 조회
//	@GetMapping
//	public ResponseEntity<List<EventDto>> getAllEvents() {
//		return ResponseEntity.ok(eventService.getAllEvents());
//	}
//
//	// 활성화된 이벤트만 조회
//	@GetMapping("/active")
//	public ResponseEntity<List<EventDto>> getActiveEvents() {
//		return ResponseEntity.ok(eventService.getActiveEvents());
//	}
//
//	// 특정 이벤트 조회
//	@GetMapping("/{id}")
//	public ResponseEntity<EventDto> getEventById(@PathVariable Long id) {
//		return ResponseEntity.ok(eventService.getEventById(id));
//	}
//
//	//이벤트에 속한 모든 상품 조회
//	@GetMapping("/{eventId}/product/list")
//	public ResponseEntity<List<EventBridgeDto>> getProductsByEventId(@PathVariable Long eventId) {
//		return ResponseEntity.ok(eventBridgeService.getProductsByEventId(eventId));
//	}
//}

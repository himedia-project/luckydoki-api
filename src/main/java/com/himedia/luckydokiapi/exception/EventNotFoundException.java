package com.himedia.luckydokiapi.exception;


public class EventNotFoundException extends RuntimeException {
	public EventNotFoundException(Long id) {
		super("이벤트를 찾을 수 없습니다. ID: " + id);
	}
}
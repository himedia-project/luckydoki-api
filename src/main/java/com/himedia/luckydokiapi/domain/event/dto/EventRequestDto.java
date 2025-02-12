package com.himedia.luckydokiapi.domain.event.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventRequestDto {
	@JsonProperty("title")
	private String title;
	
	@JsonProperty("content")
	private String content;
	private String image;
	private LocalDateTime startAt;
	private LocalDateTime endAt;
	
	// JSON 파싱 오류를 방지하기 위해 개행 문자 정리
	public void sanitize() {
		this.title = sanitizeString(this.title);
		this.content = sanitizeString(this.content);
	}
	
	private String sanitizeString(String input) {
		return input == null ? null : input.replaceAll("[\\p{Cntrl}&&[^\n\t]]", "").trim();
	}
}

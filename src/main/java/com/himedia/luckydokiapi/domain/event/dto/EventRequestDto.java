package com.himedia.luckydokiapi.domain.event.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

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

	private MultipartFile file;


	private LocalDate startAt;
	private LocalDate endAt;

	// 상품 id 갯수가 10개까지만
	@Size(max = 10)
	private List<Long> productIds;


	private String image;

	// JSON 파싱 오류를 방지하기 위해 개행 문자 정리
	public void sanitize() {
		this.title = sanitizeString(this.title);
		this.content = sanitizeString(this.content);
	}
	
	private String sanitizeString(String input) {
		return input == null ? null : input.replaceAll("[\\p{Cntrl}&&[^\n\t]]", "").trim();
	}
}

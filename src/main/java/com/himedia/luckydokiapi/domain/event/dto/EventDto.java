package com.himedia.luckydokiapi.domain.event.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventDto {
	private Long id;
	private String title;
	private String content;
	private String image;
	private LocalDateTime startAt;
	private LocalDateTime endAt;
}

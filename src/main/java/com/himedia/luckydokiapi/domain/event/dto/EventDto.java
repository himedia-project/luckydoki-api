package com.himedia.luckydokiapi.domain.event.dto;

import lombok.*;

import java.time.LocalDate;

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
	private LocalDate startAt;
	private LocalDate endAt;
}

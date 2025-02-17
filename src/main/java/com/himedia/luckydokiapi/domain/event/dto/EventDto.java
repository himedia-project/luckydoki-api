package com.himedia.luckydokiapi.domain.event.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

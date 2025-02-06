package com.himedia.luckydokiapi.domain.event.dto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventBridgeDto {
	private Long id;
	private Long eventId;
	private Long productId;
}
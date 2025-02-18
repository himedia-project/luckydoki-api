package com.himedia.luckydokiapi.domain.event.dto;

import com.himedia.luckydokiapi.domain.event.entity.Event;
import com.himedia.luckydokiapi.domain.event.entity.EventBridge;
import com.himedia.luckydokiapi.domain.product.dto.ProductDTO;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventDetailDto {

    private Long id;
    private String title;
    private String content;
    private String image;
    private LocalDate startAt;
    private LocalDate endAt;

    private List<ProductDTO.Response> productList = new ArrayList<>();

    public static EventDetailDto from(Event event) {
        return EventDetailDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .content(event.getContent())
                .image(event.getImage())
                .startAt(event.getStartAt())
                .endAt(event.getEndAt())
                .productList(event.getEventBridgeList().stream()
                        .map(EventBridge::getProduct)
                        .map(ProductDTO.Response::from)
                        .collect(Collectors.toList()))
                .build();
    }
}

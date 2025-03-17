package com.himedia.luckydokiapi.domain.search.event;

public class ProductIndexEvent extends IndexEvent {
    public ProductIndexEvent(Long id, String operation) {
        super(id, operation);
    }
} 
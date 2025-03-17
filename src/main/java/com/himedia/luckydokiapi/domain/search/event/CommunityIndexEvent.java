package com.himedia.luckydokiapi.domain.search.event;

public class CommunityIndexEvent extends IndexEvent {
    public CommunityIndexEvent(Long id, String operation) {
        super(id, operation);
    }
} 
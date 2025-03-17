package com.himedia.luckydokiapi.domain.search.service;

import com.himedia.luckydokiapi.domain.search.event.CommunityIndexEvent;
import com.himedia.luckydokiapi.domain.search.event.ProductIndexEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IndexingService {
    private final ApplicationEventPublisher eventPublisher;

    public void indexProduct(Long productId, String operation) {
        eventPublisher.publishEvent(new ProductIndexEvent(productId, operation));
    }

    public void indexCommunity(Long communityId, String operation) {
        eventPublisher.publishEvent(new CommunityIndexEvent(communityId, operation));
    }
} 
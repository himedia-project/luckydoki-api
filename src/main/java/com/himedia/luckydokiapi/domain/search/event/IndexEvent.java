package com.himedia.luckydokiapi.domain.search.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class IndexEvent {
    private final Long id;
    private final String operation; // CREATE, UPDATE, DELETE
} 
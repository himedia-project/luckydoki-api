package com.himedia.luckydokiapi.domain.search.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Service
@Slf4j
public class SearchKeywordService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String POPULAR_KEYWORDS_KEY = "popular_keywords";

    public SearchKeywordService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 검색어 저장 및 카운트 증가
    public void incrementSearchCount(String keyword) {
        log.info("SearchKeywordService incrementSearchCount keyword: {}", keyword);
        if (keyword == null || keyword.trim().isEmpty()) {
            return;
        }

        redisTemplate.opsForZSet().incrementScore(POPULAR_KEYWORDS_KEY, keyword.toLowerCase(), 1);
    }

    // 인기 검색어 가져오기 (상위 N개)
    public List<String> getPopularKeywords(int limit) {
        Set<Object> topKeywords = redisTemplate.opsForZSet().reverseRange(POPULAR_KEYWORDS_KEY, 0, limit - 1);

        List<String> result = new ArrayList<>();
        if (topKeywords != null) {
            for (Object keyword : topKeywords) {
                result.add((String) keyword);
            }
        }

        return result;
    }

    // 검색어와 검색 횟수를 함께 가져오기
    public List<KeywordCount> getPopularKeywordsWithCount(int limit) {
        Set<ZSetOperations.TypedTuple<Object>> topKeywords =
                redisTemplate.opsForZSet().reverseRangeWithScores(POPULAR_KEYWORDS_KEY, 0, limit - 1);

        List<KeywordCount> result = new ArrayList<>();
        if (topKeywords != null) {
            for (ZSetOperations.TypedTuple<Object> tuple : topKeywords) {
                String keyword = (String) tuple.getValue();
                Double score = tuple.getScore();
                result.add(new KeywordCount(keyword, score != null ? score.longValue() : 0));
            }
        }

        return result;
    }

    // 검색어와 카운트를 담는 DTO
    public static class KeywordCount {
        private String keyword;
        private long count;

        public KeywordCount(String keyword, long count) {
            this.keyword = keyword;
            this.count = count;
        }

        // Getters and setters
        public String getKeyword() { return keyword; }
        public void setKeyword(String keyword) { this.keyword = keyword; }
        public long getCount() { return count; }
        public void setCount(long count) { this.count = count; }
    }
}
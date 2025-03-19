package com.himedia.luckydokiapi.security.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
@RequiredArgsConstructor
public class TokenRepository {

    private final RedisTemplate<String, String> redisTemplate;
    
    private static final String ACCESS_TOKEN_PREFIX = "access:";
    private static final String REFRESH_TOKEN_PREFIX = "refresh:";
    private static final String BLACKLIST_PREFIX = "blacklist:";
    
    /**
     * 리프레시 토큰 저장
     */
    public void saveRefreshToken(String email, String refreshToken, long expirationTime) {
        String key = REFRESH_TOKEN_PREFIX + email;
        redisTemplate.opsForValue().set(key, refreshToken, expirationTime, TimeUnit.MILLISECONDS);
        log.info("리프레시 토큰 저장: {}", email);
    }
    
    /**
     * 리프레시 토큰 조회
     */
    public String getRefreshToken(String email) {
        String key = REFRESH_TOKEN_PREFIX + email;
        return redisTemplate.opsForValue().get(key);
    }
    
    /**
     * 리프레시 토큰 삭제 (로그아웃 시)
     */
    public void deleteRefreshToken(String email) {
        String key = REFRESH_TOKEN_PREFIX + email;
        redisTemplate.delete(key);
        log.info("리프레시 토큰 삭제: {}", email);
    }
    
    /**
     * 액세스 토큰 블랙리스트에 추가 (로그아웃 시)
     */
    public void addToBlacklist(String accessToken, long remainingTimeInMillis) {
        String key = BLACKLIST_PREFIX + accessToken;
        redisTemplate.opsForValue().set(key, "logout", remainingTimeInMillis, TimeUnit.MILLISECONDS);
        log.info("액세스 토큰 블랙리스트(로그아웃) 추가: {}", accessToken);
    }
    
    /**
     * 토큰이 블랙리스트에 있는지 확인
     */
    public boolean isBlacklisted(String accessToken) {
        String key = BLACKLIST_PREFIX + accessToken;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
} 
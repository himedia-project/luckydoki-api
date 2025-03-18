package com.himedia.luckydokiapi.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@EnableCaching
@Configuration
public class RedisConfig {


    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 인코딩을 명시적으로 지정한 StringRedisSerializer
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer(StandardCharsets.UTF_8);

        // JSON 직렬화 설정
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper());

        // 직렬화 설정 적용
        template.setKeySerializer(stringRedisSerializer);
        template.setValueSerializer(jackson2JsonRedisSerializer);  // String 대신 JSON 사용
        template.setHashKeySerializer(stringRedisSerializer);
        template.setHashValueSerializer(jackson2JsonRedisSerializer);  // String 대신 JSON 사용

        template.afterPropertiesSet();
        return template;
    }

    // Redis 캐시 키 상수 정의
    public static final String PRODUCT_DETAIL = "product_detail";
    public static final String PRODUCT_LIST = "product_list";
    public static final String COMMUNITY_DETAIL = "community_detail";
    public static final String COMMUNITY_LIST = "community_list";
    public static final String COMMUNITY_PAGE = "community_page";
    
    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        serializer.setObjectMapper(objectMapper);
        
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext
                        .SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext
                        .SerializationPair
                        .fromSerializer(serializer));

        Map<String, RedisCacheConfiguration> cacheConfigurationMap = new HashMap<>();
        
        // 상품 상세 조회는 10분 캐시
        cacheConfigurationMap.put(PRODUCT_DETAIL, 
                redisCacheConfiguration.entryTtl(Duration.ofMinutes(10L)));
        
        // 상품 목록 조회는 5분 캐시
        cacheConfigurationMap.put(PRODUCT_LIST, 
                redisCacheConfiguration.entryTtl(Duration.ofMinutes(5L)));

        // 커뮤니티 상세 조회는 10분 캐시
        cacheConfigurationMap.put(COMMUNITY_DETAIL,
                redisCacheConfiguration.entryTtl(Duration.ofMinutes(10L)));

        // 커뮤니티 목록 조회는 5분 캐시
        cacheConfigurationMap.put(COMMUNITY_LIST,
                redisCacheConfiguration.entryTtl(Duration.ofMinutes(5L)));

        // 커뮤니티 페이징 목록 조회는 5분 캐시
        cacheConfigurationMap.put(COMMUNITY_PAGE,
                redisCacheConfiguration.entryTtl(Duration.ofMinutes(5L)));

        return RedisCacheManager
                .RedisCacheManagerBuilder
                .fromConnectionFactory(redisConnectionFactory)
                .cacheDefaults(redisCacheConfiguration)
                .withInitialCacheConfigurations(cacheConfigurationMap)
                .build();
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        
        // Java 8 날짜/시간 모듈 등록
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        // 알 수 없는 속성에 대한 예외 무시
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return mapper;
    }
}

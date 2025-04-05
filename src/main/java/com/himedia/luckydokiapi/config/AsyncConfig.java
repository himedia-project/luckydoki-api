package com.himedia.luckydokiapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * spring에서 관리하는 스레드 풀을 생성하는 메서드
     * @return 스레드 풀을 생성하는 TaskExecutor
     *  ⭐Spring 관리 생명주기로 종료 시 자동 정리
     */
    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);    // 기본적으로 항상 유지되는 코어 스레드 수 (2개)
        executor.setMaxPoolSize(5);     // 최대 생성 가능한 스레드 수 (5개)
        executor.setQueueCapacity(25);  // 대기 큐 용량 (25개 작업까지 대기 가능)
        executor.setKeepAliveSeconds(60); // 유휴 스레드 60초 후 제거
        executor.setThreadNamePrefix("async-"); // 스레드 이름에 "async-"" prefix 추가로 디버깅 용이성 증가
        executor.initialize();
        return executor;
    }
} 
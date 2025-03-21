package com.himedia.luckydokiapi.domain.coupon.producer;

import com.himedia.luckydokiapi.domain.coupon.dto.CouponMassIssueRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    @Value("${coupon.kafka.topics.issue}")
    private String couponIssueTopic;
    
    /**
     * 대용량 쿠폰 발급 요청을 Kafka에 전송
     * @param couponId 쿠폰 ID
     * @param emails 발급 대상 이메일 목록
     * @return 요청 ID
     */
    public String sendMassIssueRequest(Long couponId, List<String> emails) {
        String requestId = UUID.randomUUID().toString();
        
        CouponMassIssueRequestDTO requestDTO = CouponMassIssueRequestDTO.builder()
                .couponId(couponId)
                .emails(emails)
                .requestId(requestId)
                .build();
        
        log.info("Sending mass coupon issue request: couponId={}, emailCount={}, requestId={}",
                couponId, emails.size(), requestId);
        
        CompletableFuture<SendResult<String, Object>> future = 
                kafkaTemplate.send(couponIssueTopic, requestId, requestDTO);
        
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Sent message: topic={}, partition={}, offset={}", 
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("Unable to send message: {}", ex.getMessage(), ex);
            }
        });
        
        return requestId;
    }
} 
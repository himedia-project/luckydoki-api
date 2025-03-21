package com.himedia.luckydokiapi.domain.coupon.consumer;

import com.himedia.luckydokiapi.domain.coupon.dto.CouponMassIssueRequestDTO;
import com.himedia.luckydokiapi.domain.coupon.service.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponConsumer {

    private final CouponService couponService;
    
    @KafkaListener(
            topics = "${coupon.kafka.topics.issue}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeCouponIssueRequest(CouponMassIssueRequestDTO requestDTO) {
        log.info("Received coupon issue request: couponId={}, emailCount={}, requestId={}",
                requestDTO.getCouponId(), requestDTO.getEmails().size(), requestDTO.getRequestId());
        
        try {
            // 각 이메일에 대해 쿠폰 발급 처리
            couponService.processMassIssue(requestDTO.getCouponId(), requestDTO.getEmails(), requestDTO.getRequestId());
            log.info("Successfully processed coupon issue request: requestId={}", requestDTO.getRequestId());
        } catch (Exception e) {
            log.error("Failed to process coupon issue request: requestId={}, error={}",
                    requestDTO.getRequestId(), e.getMessage(), e);
        }
    }
} 
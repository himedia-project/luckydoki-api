package com.himedia.luckydokiapi.domain.coupon.service;

import com.himedia.luckydokiapi.domain.coupon.dto.CouponRecordResponseDTO;
import com.himedia.luckydokiapi.domain.coupon.dto.CouponRecordSearchDTO;
import com.himedia.luckydokiapi.domain.coupon.dto.CouponRequestDto;
import com.himedia.luckydokiapi.domain.coupon.dto.CouponResponseDTO;
import com.himedia.luckydokiapi.domain.coupon.entity.Coupon;
import com.himedia.luckydokiapi.domain.coupon.entity.CouponRecord;
import com.himedia.luckydokiapi.domain.coupon.enums.CouponRecordStatus;
import com.himedia.luckydokiapi.domain.coupon.enums.CouponStatus;
import com.himedia.luckydokiapi.domain.coupon.producer.CouponProducer;
import com.himedia.luckydokiapi.domain.coupon.repository.CouponRecordRepository;
import com.himedia.luckydokiapi.domain.coupon.repository.CouponRepository;
import com.himedia.luckydokiapi.domain.member.entity.Member;
import com.himedia.luckydokiapi.domain.member.repository.MemberRepository;
import com.himedia.luckydokiapi.domain.notification.enums.NotificationType;
import com.himedia.luckydokiapi.domain.notification.service.NotificationService;
import com.himedia.luckydokiapi.dto.PageResponseDTO;
import com.himedia.luckydokiapi.util.NumberGenerator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;

    private final CouponRecordRepository couponRecordRepository;

    private final MemberRepository memberRepository;

    private final NotificationService notificationService;

    private final CouponProducer couponProducer;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String COUPON_ISSUE_PROCESSING_KEY = "coupon:issue:processing:";
    private static final String COUPON_ISSUE_COMPLETED_KEY = "coupon:issue:completed:";

    @Transactional(readOnly = true)
    @Override
    public PageResponseDTO<CouponResponseDTO> getAllCoupons(CouponRequestDto requestDto) {
        Page<Coupon> result = couponRepository.findListBy(requestDto);

        return PageResponseDTO.<CouponResponseDTO>withAll()
                .dtoList(result.getContent().stream().map(CouponResponseDTO::from).toList())
                .totalCount(result.getTotalElements())
                .pageRequestDTO(requestDto)
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public List<CouponResponseDTO> getActiveCoupons() {
        return couponRepository.findActiveCoupons().stream()
                .map(CouponResponseDTO::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public CouponResponseDTO getCouponById(Long id) {
        Coupon coupon = couponRepository.findCouponById(id)
                .orElseThrow(() -> new EntityNotFoundException("Coupon not found with id: " + id));
        return CouponResponseDTO.from(coupon);
    }

    @Transactional(readOnly = true)
    @Override
    public CouponResponseDTO getCouponByCode(String code) {
        Coupon coupon = couponRepository.findByCode(code);
        if (coupon == null) {
            throw new EntityNotFoundException("Coupon not found with code: " + code);
        }
        return CouponResponseDTO.from(coupon);
    }

    @Override
    public Long createCoupon(CouponRequestDto requestDto) {
        Coupon coupon = Coupon.builder()
                .code(NumberGenerator.generateRandomNumber(10)) // ✅ 랜덤 코드 생성
                .name(requestDto.getName())
                .content(requestDto.getContent())
                .discountPrice(requestDto.getDiscountPrice())
                .minimumUsageAmount(requestDto.getMinimumUsageAmount())
                .status(CouponStatus.ACTIVE) // ✅ 활성화 상태로 자동 설정
                .startDate(requestDto.getStartDate()) // ✅ 현재 날짜 자동 설정
                .endDate(requestDto.getEndDate()) // ✅ 3개월 후 자동 설정
                .build();

        Coupon savedCoupon = couponRepository.save(coupon);
        return savedCoupon.getId();
    }

    @Override
    public CouponResponseDTO updateCoupon(Long id, CouponRequestDto requestDto) {
        // ✅ QueryDSL 기반의 updateCoupon() 호출
        couponRepository.updateCoupon(id, requestDto);
        return getCouponById(id);
    }

    @Override
    public void deleteCoupon(Long couponId) {
        Coupon coupon = getCoupon(couponId);

        // 해당 쿠폰이 발급되어 있다면 예외처리
        if (couponRecordRepository.existsByCouponId(couponId)) {
            throw new IllegalArgumentException("Coupon issued to members, couponId: " + couponId);
        }

        // ✅ 쿠폰 삭제 시 쿠폰 발급 내역 삭제
        couponRepository.delete(coupon);
    }

    @Transactional(readOnly = true)
    @Override
    public PageResponseDTO<CouponRecordResponseDTO> getCouponRecords(CouponRecordSearchDTO requestDto) {
        Page<CouponRecord> result = couponRecordRepository.findListBy(requestDto);

        return PageResponseDTO.<CouponRecordResponseDTO>withAll()
                .dtoList(result.getContent().stream().map(CouponRecordResponseDTO::from).toList())
                .totalCount(result.getTotalElements())
                .pageRequestDTO(requestDto)
                .build();
    }


    /**
     * 단일 사용자에게 쿠폰 발급 처리 (내부 메서드)
     *
     * @param couponId 쿠폰 ID
     * @param email    사용자 이메일
     */
    public void issueCouponToUser(Long couponId, String email) {
        // 멤버 ID 조회 및 쿠폰 발급 로직 구현
        Coupon coupon = getCoupon(couponId);
        // 실제 구현 시에는 Member 서비스와 연동하여 email로 userId를 찾아야 함
        // 여기서는 예시로만 작성
        Member member = memberRepository.getWithRoles(email)
                .orElseThrow(() -> new EntityNotFoundException("Member not found with email: " + email));

        // 이미 발급 받았는지 확인
        if (couponRecordRepository.existsByCouponIdAndMemberEmail(coupon.getId(), email)) {
            throw new IllegalArgumentException("Coupon already issued to member: " + email);
        }
        // 쿠폰 수량 체크
/*            Coupon coupon = getCoupon(couponId);
            if (coupon.getStock() <= 0) {
                throw new IllegalArgumentException("Coupon out of stock: " + couponId);
            }*/
        // 쿠폰 발급 처리
        CouponRecord couponRecord = CouponRecord.builder()
                .coupon(coupon)
                .member(member)
                .status(CouponRecordStatus.UNUSED)
                .build();
        couponRecordRepository.save(couponRecord);
        // ✅ 쿠폰 발급 시 알림 발송
        if (coupon.getId().equals(1L)) {
            notificationService.sendWelcomeCouponToMember(member);
        } else {
            notificationService.sendCouponToMember(member, coupon.getName(), coupon.getContent(), NotificationType.COUPON_ISSUE);
        }

        coupon.changeStatus(CouponStatus.ISSUED);

    }

    @Override
    public void issueCoupon(Long couponId, List<String> memberEmails) {
        // 쿠폰 존재 여부 확인
        Coupon coupon = getCoupon(couponId);
        // 각 이메일에 대해 쿠폰 발급 처리
        for (String email : memberEmails) {
            this.issueCouponToUser(coupon.getId(), email);
        }
    }

    /**
     * 쿠폰 조회
     *
     * @param couponId 쿠폰 ID
     * @return 쿠폰
     */
    public Coupon getCoupon(Long couponId) {
        return couponRepository.findById(couponId)
                .orElseThrow(() -> new EntityNotFoundException("Coupon not found with id: " + couponId));
    }

    /**
     * 회원의 쿠폰 목록 조회
     *
     * @param email 회원 이메일
     * @return 쿠폰 목록
     */
    @Transactional(readOnly = true)
    @Override
    public List<CouponResponseDTO> getCouponList(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Member not found with email: " + email));
        return couponRecordRepository.findCouponListByMemberEmail(member.getEmail()).stream()
                .map(CouponResponseDTO::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public int countAllCoupon() {
        return couponRepository.findAll().size();
    }


    /**
     * 회원가입 쿠폰 생성
     */
    @Override
    public void createWelcomeCoupon() {
        // ✅ 쿠폰 초기화
        // 회원가입 쿠폰 생성
        // INSERT INTO `coupon` (`end_date`, `start_date`, `id`,`code`, `content`, `name`, `status`, `discount_price`, `minimum_usage_amount`) VALUES ('2026-02-12', '2025-02-12', 1, '3285037658', '😊첫회원가입축하쿠폰! 3000원 할인이 됩니다!', '🎉회원가입축하쿠폰', 'ACTIVE', 3000, 30000);
        Coupon coupon = Coupon.builder()
                .code(NumberGenerator.generateRandomNumber(10))
                .name("🎉회원가입축하쿠폰")
                .content("😊첫회원가입축하쿠폰! 3000원 할인이 됩니다!")
                .discountPrice(3000)
                .minimumUsageAmount(30000)
                .status(CouponStatus.ACTIVE)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(12))
                .build();
        couponRepository.save(coupon);
    }

    /**
     * 쿠폰 사용 -> 쿠폰발급 목록에 사용일시 기록
     *
     * @param email  회원 이메일
     * @param coupon 쿠폰
     */
    @Override
    public void useCoupon(String email, Coupon coupon) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Member not found with email: " + email));
        CouponRecord couponRecord = couponRecordRepository.findByMemberAndCoupon(member.getEmail(), coupon)
                .orElseThrow(() -> new EntityNotFoundException("Coupon not found with member: " + email));
        couponRecord.useCoupon();
    }

    // kafka 쿠폰 발급 처리
    /**
     * 대용량 쿠폰 발급 요청 처리 - Kafka로 전송
     *
     * @param couponId 쿠폰 ID
     * @param emails   발급 대상 이메일 목록
     * @return 요청 ID
     */
    public String requestMassIssue(Long couponId, List<String> emails) {
        // 쿠폰 존재 여부 확인
        getCouponById(couponId);

        // 대용량 발급 요청을 Kafka로 전송
        String requestId = couponProducer.sendMassIssueRequest(couponId, emails);

        // Redis에 처리 상태 저장 (처리 중)
        // 1일 후 자동 삭제
        redisTemplate.opsForValue().set(
                COUPON_ISSUE_PROCESSING_KEY + requestId,
                String.valueOf(emails.size()),
                1,
                TimeUnit.DAYS
        );

        return requestId;
    }

    @Transactional(readOnly = true)
    @Override
    public String getIssueStatus(String requestId) {
        // 처리 완료된 요청인지 확인
        String completedValue = (String) redisTemplate.opsForValue().get(COUPON_ISSUE_COMPLETED_KEY + requestId);
        if (completedValue != null) {
            return "COMPLETED:" + completedValue; // 완료 상태와 성공 개수 반환
        }

        // 처리 중인 요청인지 확인
        String processingValue = (String) redisTemplate.opsForValue().get(COUPON_ISSUE_PROCESSING_KEY + requestId);
        if (processingValue != null) {
            return "PROCESSING:" + processingValue; // 처리 중 상태와 총 개수 반환
        }

        // 요청 ID가 존재하지 않는 경우
        return "NOT_FOUND";
    }


    /**
     * Kafka Consumer에서 호출하는 대용량 발급 처리 메서드
     *
     * @param couponId  쿠폰 ID
     * @param emails    발급 대상 이메일 목록
     * @param requestId 요청 ID
     */
    public void processMassIssue(Long couponId, List<String> emails, String requestId) {
        // 이미 처리 완료된 요청인지 확인
        String completedKey = COUPON_ISSUE_COMPLETED_KEY + requestId;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(completedKey))) {
            log.info("Request already processed: {}", requestId);
            return;
        }

        int successCount = 0;

        // 각 이메일에 대해 쿠폰 발급 처리
        for (String email : emails) {
            try {
                issueCouponToUser(couponId, email);
                successCount++;
            } catch (Exception e) {
                log.error("Failed to issue coupon to user: couponId={}, email={}, error={}",
                        couponId, email, e.getMessage());
                // 실패해도 계속 진행
            }
        }

        // 처리 결과 저장
        redisTemplate.opsForValue().set(completedKey, String.valueOf(successCount), 7, TimeUnit.DAYS);
        redisTemplate.delete(COUPON_ISSUE_PROCESSING_KEY + requestId);

        log.info("Mass coupon issue completed: requestId={}, total={}, success={}",
                requestId, emails.size(), successCount);
    }



}

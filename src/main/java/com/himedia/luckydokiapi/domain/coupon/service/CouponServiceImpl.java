package com.himedia.luckydokiapi.domain.coupon.service;

import com.himedia.luckydokiapi.domain.coupon.dto.CouponRecordResponseDTO;
import com.himedia.luckydokiapi.domain.coupon.dto.CouponRecordSearchDTO;
import com.himedia.luckydokiapi.domain.coupon.dto.CouponRequestDto;
import com.himedia.luckydokiapi.domain.coupon.dto.CouponResponseDto;
import com.himedia.luckydokiapi.domain.coupon.entity.Coupon;
import com.himedia.luckydokiapi.domain.coupon.entity.CouponRecord;
import com.himedia.luckydokiapi.domain.coupon.enums.CouponRecordStatus;
import com.himedia.luckydokiapi.domain.coupon.enums.CouponStatus;
import com.himedia.luckydokiapi.domain.coupon.repository.CouponRecordRepository;
import com.himedia.luckydokiapi.domain.coupon.repository.CouponRepository;
import com.himedia.luckydokiapi.domain.member.service.MemberService;
import com.himedia.luckydokiapi.dto.PageResponseDTO;
import com.himedia.luckydokiapi.util.NumberGenerator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CouponServiceImpl implements CouponService {

	private final CouponRepository couponRepository;

	private final CouponRecordRepository couponRecordRepository;

	private final MemberService memberService;

	@Override
	public PageResponseDTO<CouponResponseDto> getAllCoupons(CouponRequestDto requestDto) {
		Page<Coupon> result = couponRepository.findListBy(requestDto);

		return PageResponseDTO.<CouponResponseDto>withAll()
				.dtoList(result.getContent().stream().map(this::convertToResponseDto).toList())
				.totalCount(result.getTotalElements())
				.pageRequestDTO(requestDto)
				.build();
	}
	
	@Override
	public List<CouponResponseDto> getActiveCoupons() {
		return couponRepository.findActiveCoupons().stream()
				.map(this::convertToResponseDto)
				.collect(Collectors.toList());
	}
	
	@Override
	public CouponResponseDto getCouponById(Long id) {
		Coupon coupon = couponRepository.findCouponById(id)
				.orElseThrow(() -> new EntityNotFoundException("Coupon not found with id: " + id));
		return convertToResponseDto(coupon);
	}
	
	@Override
	public CouponResponseDto getCouponByCode(String code) {
		Coupon coupon = couponRepository.findByCode(code);
		if (coupon == null) {
			throw new EntityNotFoundException("Coupon not found with code: " + code);
		}
		return convertToResponseDto(coupon);
	}
	
	@Override
	@Transactional
	public Long createCoupon(CouponRequestDto requestDto) {
		Coupon coupon = Coupon.builder()
				.code(NumberGenerator.generateRandomNumber(10)) // ✅ 랜덤 코드 생성
				.name(requestDto.getName())
				.content(requestDto.getContent())
				.discountPrice(requestDto.getDiscountPrice())
				.status(CouponStatus.ACTIVE) // ✅ 활성화 상태로 자동 설정
				.startDate(requestDto.getStartDate()) // ✅ 현재 날짜 자동 설정
				.endDate(requestDto.getEndDate()) // ✅ 3개월 후 자동 설정
				.build();
		
		Coupon savedCoupon = couponRepository.save(coupon);
		return savedCoupon.getId();
	}
	
	@Override
	@Transactional
	public CouponResponseDto updateCoupon(Long id, CouponRequestDto requestDto) {
		// ✅ QueryDSL 기반의 updateCoupon() 호출
		couponRepository.updateCoupon(id, requestDto);
		return getCouponById(id);
	}
	
	@Override
	@Transactional
	public void deleteCoupon(Long couponId) {
		Coupon coupon = getCoupon(couponId);

		// 해당 쿠폰이 발급되어 있다면 예외처리
		if (couponRecordRepository.existsByCouponId(couponId)) {
			throw new IllegalArgumentException("Coupon issued to members, couponId: " + couponId);
		}

		// ✅ 쿠폰 삭제 시 쿠폰 발급 내역 삭제
		couponRepository.delete(coupon);
	}

	@Override
	public PageResponseDTO<CouponRecordResponseDTO> getCouponRecords(CouponRecordSearchDTO requestDto) {
		Page<CouponRecord> result = couponRecordRepository.findListBy(requestDto);

		return PageResponseDTO.<CouponRecordResponseDTO>withAll()
				.dtoList(result.getContent().stream().map(this::convertToRecordResponseDto).toList())
				.totalCount(result.getTotalElements())
				.pageRequestDTO(requestDto)
				.build();
	}

	@Transactional
	@Override
	public void issueCoupon(Long couponId, List<String> memberEmails) {
		Coupon coupon = getCoupon(couponId);

		memberEmails.forEach(email -> {
			// 쿠폰 중복 발급여부 확인
			if (couponRecordRepository.existsByCouponIdAndMemberEmail(couponId, email)) {
				throw new IllegalArgumentException("Coupon already issued to member: " + email);
			}

			CouponRecord couponRecord = CouponRecord.builder()
					.coupon(coupon)
					.member(memberService.getEntity(email))
					.issuedAt(LocalDateTime.now())
					.status(CouponRecordStatus.UNUSED)
					.build();
			couponRecordRepository.save(couponRecord);
		});
	}

	/**
	 * 쿠폰 조회
	 * @param couponId 쿠폰 ID
	 * @return 쿠폰
	 */
	private Coupon getCoupon(Long couponId) {
		return couponRepository.findById(couponId)
				.orElseThrow(() -> new EntityNotFoundException("Coupon not found with id: " + couponId));
	}

	private CouponResponseDto convertToResponseDto(Coupon coupon) {
		return CouponResponseDto.builder()
				.id(coupon.getId())
				.code(coupon.getCode())
				.name(coupon.getName())
				.content(coupon.getContent())
				.discountPrice(coupon.getDiscountPrice())
				.startDate(coupon.getStartDate())
				.endDate(coupon.getEndDate())
				.status(coupon.getStatus())
				.build();
	}

	private CouponRecordResponseDTO convertToRecordResponseDto(CouponRecord couponRecord) {
		return CouponRecordResponseDTO.builder()
				.id(couponRecord.getId())
				.name(couponRecord.getCoupon().getName())
				.code(couponRecord.getCoupon().getCode())
				.email(couponRecord.getMember().getEmail())
				.issuedAt(couponRecord.getIssuedAt())
				.expiredAt(couponRecord.getExpiredAt())
				.used(couponRecord.getUsedDatetime() != null)
				// ✅ 쿠폰 유효기간 계산
				.validPeriod(ChronoUnit.DAYS.between(couponRecord.getCoupon().getStartDate(), couponRecord.getCoupon().getEndDate()) + 1)
				.build();
	}
}

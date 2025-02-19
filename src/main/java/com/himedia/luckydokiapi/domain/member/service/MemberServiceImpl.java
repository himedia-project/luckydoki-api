package com.himedia.luckydokiapi.domain.member.service;


import com.himedia.luckydokiapi.domain.coupon.service.CouponService;
import com.himedia.luckydokiapi.domain.member.dto.JoinRequestDTO;
import com.himedia.luckydokiapi.domain.member.dto.MemberDetailDTO;
import com.himedia.luckydokiapi.domain.member.dto.SellerRequestDTO;
import com.himedia.luckydokiapi.domain.member.dto.UpdateMemberDTO;
import com.himedia.luckydokiapi.domain.member.entity.Member;
import com.himedia.luckydokiapi.domain.member.entity.SellerApplication;
import com.himedia.luckydokiapi.domain.member.enums.MemberActive;
import com.himedia.luckydokiapi.domain.member.enums.ShopApproved;
import com.himedia.luckydokiapi.domain.member.repository.MemberRepository;
import com.himedia.luckydokiapi.domain.notification.service.NotificationService;
import com.himedia.luckydokiapi.domain.phone.service.PhoneVerificationService;
import com.himedia.luckydokiapi.domain.shop.entity.Shop;
import com.himedia.luckydokiapi.domain.shop.repository.ShopRepository;
import com.himedia.luckydokiapi.props.JwtProps;
import com.himedia.luckydokiapi.security.CustomUserDetailService;
import com.himedia.luckydokiapi.security.MemberDTO;
import com.himedia.luckydokiapi.util.JWTUtil;
import com.himedia.luckydokiapi.util.file.CustomFileUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final SellerApplicationRepository sellerApplicationRepository;
    private final JWTUtil jwtUtil;
    private final JwtProps jwtProps;
    private final CustomUserDetailService userDetailService;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomFileUtil fileUtil;
    private final ShopRepository shopRepository;
    private final PhoneVerificationService phoneVerificationService;
    private final CouponService couponService;

    private final NotificationService notificationService;


    @Transactional(readOnly = true)
    @Override
    public Map<String, Object> login(String email, String password) {
        MemberDTO memberAuthDTO = (MemberDTO) userDetailService.loadUserByUsername(email);
        log.info("email :{} , password :{} ", email, password);

        if(memberAuthDTO.getActive() == MemberActive.N){
            throw new RuntimeException("탈퇴한 회원은 로그인할 수 없습니다.");
        }

        if (!passwordEncoder.matches(password, memberAuthDTO.getPassword())) {
            throw new RuntimeException("비밀번호가 틀렸습니다.");

        }

        Map<String, Object> memberClaims = memberAuthDTO.getClaims();

        String accessToken = jwtUtil.generateToken(memberClaims, jwtProps.getAccessTokenExpirationPeriod());
        String refreshToken = jwtUtil.generateToken(memberClaims, jwtProps.getRefreshTokenExpirationPeriod());

        memberClaims.put("accessToken", accessToken);
        memberClaims.put("refreshToken", refreshToken);
        memberClaims.put("active", memberAuthDTO.getActive().name());

        return memberClaims;
    }

    @Override
    public void join(JoinRequestDTO request) {
        memberRepository.findByEmail(request.getEmail())
                .ifPresent(member -> {
                    throw new IllegalArgumentException("이미 존재하는 회원입니다!");
                });

        boolean isVerified = phoneVerificationService.verifyCode(request.getPhone(), request.getVerificationCode());
        if (!isVerified) {
            throw new IllegalArgumentException("전화번호 인증이 실패했습니다.");
        }

        request.setPassword(passwordEncoder.encode(request.getPassword()));
        Member member = Member.from(request);
        // 먼저 회원을 저장
        memberRepository.save(member);

        // 회원 저장 후 쿠폰 발급 처리
        try {
            couponService.issueCoupon(1L, List.of(member.getEmail()));
        } catch (Exception e) {
            log.error("Failed to issue welcome coupon for member: " + member.getEmail(), e);
            // 쿠폰 발급 실패는 회원가입 실패로 이어지지 않도록 함
        }
    }

    /**
     * 회원정보 Entity -> DTO 변환
     *
     * @param member 회원정보
     * @return 회원정보 DTO
     */
    public MemberDTO entityToDTO(Member member) {

        return new MemberDTO(
                member.getEmail(),
                member.getPassword(),
                member.getPhone(),
                member.getNickName(),
                member.getMemberRoleList().stream()
                        .map(Enum::name).toList(),
                member.getActive()

        );
    }

    @Override
    public String makeTempPassword() {
        StringBuffer buffer = new StringBuffer();

        for (int i = 0; i < 10; i++) {
            buffer.append((char) ((int) (Math.random() * 55) + 65));
        }
        return buffer.toString();
    }

    @Override
    public Map<String, Object> getSocialClaims(MemberDTO memberDTO) {
        return Map.of();
    }

    @Override
    public Member getEntity(String email) {
        return memberRepository.getWithRoles(email)
                .orElseThrow(() -> new EntityNotFoundException("해당하는 회원이 없습니다. email: " + email));
    }

    @Transactional(readOnly = true)
    @Override
    public MemberDetailDTO getMyInfo(String email) {
        Member member = getEntity(email);
        Shop seller = getSeller(member.getEmail());
        // 셀러 신청 여부 확인
        Boolean result = sellerApplicationRepository.existsByEmail(email);
        return entityToMemberDetailDTO(member, seller, result);
    }


    @Override
    public MemberDetailDTO updateMyInfo(String email, UpdateMemberDTO request) {
        Member member = getEntity(email);


        if (request.getNickName() != null && !request.getNickName().isEmpty()) {
            member.updateNickName(request.getNickName());
        }

        if (request.getPhone() != null && !request.getPhone().isEmpty()) {
            member.updatePhone(request.getPhone());
        }
        memberRepository.save(member);

        Shop seller = getSeller(member.getEmail());
        return entityToMemberDetailDTO(member, seller, null);

    }


    /**
     * 셀러 신청과 동시에 DB에 저장
     */
    public Long upgradeToSeller(String email, SellerRequestDTO requestDTO) {
        Member member = memberRepository.findById(email)
                .orElseThrow(() -> new EntityNotFoundException("해당 회원이 존재하지 않습니다. email: " + email));

        boolean alreadyExists = sellerApplicationRepository.findByEmail(email).isPresent();
        if (alreadyExists) {
            throw new IllegalStateException("이미 셀러 신청을 완료한 회원입니다.");
        }

        if (requestDTO.getProfileImage() == null || requestDTO.getProfileImage().isEmpty()) {
            throw new IllegalArgumentException("프로필 이미지는 필수 입력값입니다.");
        }

        // 파일을 업로드하고 DB에 저장할 파일 경로를 설정
        String uploadedImagePath = fileUtil.uploadS3File(requestDTO.getProfileImage());

        // shopImage 값을 설정한 상태에서 SellerApplication 객체 생성
        SellerApplication application = SellerApplication.builder()
                .email(member.getEmail())
                .nickName(member.getNickName())
                .introduction(requestDTO.getIntroduction())
                .shopImage(uploadedImagePath)  // 여기서 바로 설정
                .approved(ShopApproved.N)
                .build();

        SellerApplication saved = sellerApplicationRepository.save(application);

        // 알림신청
//        notificationService.sendSellerApprovalNotification(member.getEmail());

        return saved.getId();
    }


    private Shop getSeller(String email) {

        return shopRepository.findByMemberEmail(email).orElse(null);
    }


    @Transactional
    public void deleteMember(String email) {

        deactivateMember(email); // 회원 비활성화
    }

    @Transactional
    public void deactivateMember(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));

        member.deactivate(); // 회원 비활성화
    }

    @Transactional
    @Override
    public void updateFCMToken(String targetEmail, String fcmToken) {
        Member member = memberRepository.findByEmail(targetEmail)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다. email: " + targetEmail));

        member.updateFcmToken(fcmToken);
    }

}

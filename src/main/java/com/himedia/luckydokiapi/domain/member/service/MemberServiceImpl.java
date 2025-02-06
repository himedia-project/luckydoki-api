package com.himedia.luckydokiapi.domain.member.service;


import com.himedia.luckydokiapi.domain.member.dto.JoinRequestDTO;
import com.himedia.luckydokiapi.domain.member.dto.SellerRequestDTO;
import com.himedia.luckydokiapi.domain.member.dto.SellerResponseDTO;
import com.himedia.luckydokiapi.domain.member.entity.Member;
import com.himedia.luckydokiapi.domain.member.entity.SellerApplication;
import com.himedia.luckydokiapi.domain.member.enums.MemberRole;
import com.himedia.luckydokiapi.domain.member.repository.MemberRepository;
import com.himedia.luckydokiapi.domain.member.repository.SellerApplicationRepository;
import com.himedia.luckydokiapi.props.JwtProps;
import com.himedia.luckydokiapi.security.CustomUserDetailService;
import com.himedia.luckydokiapi.security.MemberDTO;
import com.himedia.luckydokiapi.util.JWTUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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


    @Transactional(readOnly = true)
    @Override
    public Map<String, Object> login(String email, String password) {
        MemberDTO memberAuthDTO = (MemberDTO) userDetailService.loadUserByUsername(email);
        log.info("로그인 dto 어쩌구 깽꺵이: " + memberAuthDTO);

        if (!passwordEncoder.matches(password, memberAuthDTO.getPassword())) {
            throw new RuntimeException("비번틀림");

        }

        Map<String, Object> memberClaims = memberAuthDTO.getClaims();

        String accessToken = jwtUtil.generateToken(memberClaims, jwtProps.getAccessTokenExpirationPeriod());
        String refreshToken = jwtUtil.generateToken(memberClaims, jwtProps.getRefreshTokenExpirationPeriod());

        memberClaims.put("access_token", accessToken);
        memberClaims.put("refresh_token", refreshToken);

        return memberClaims;
    }

    @Override
    public void join(JoinRequestDTO request) {

        memberRepository.findByEmail(request.getEmail())
                .ifPresent(member -> {
                    throw new IllegalArgumentException("이미 존재하는 회원입니다!");
                });

            Member member = Member.builder()
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .nickName(request.getNickName())
                    .build();

            member.addRole(MemberRole.USER);

            memberRepository.save(member);
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
                member.getNickName(),
                member.getMemberRoleList().stream()
                        .map(Enum::name).toList());
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
                .orElseThrow(() -> new EntityNotFoundException("해당하는 회원이 없습니다."));
    }

    /**
     *  셀러 신청과 동시에 DB에 저장
     */
    @Override
    public SellerResponseDTO upgradeToSeller(SellerRequestDTO requestDTO) {
        // 1. 회원 정보 조회 (이메일 기준)
        Member member = memberRepository.findById(requestDTO.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("해당 회원이 존재하지 않습니다. email: " + requestDTO.getEmail()));

        // 2. 이미 신청했는지 확인 (중복 방지)
        boolean alreadyExists = sellerApplicationRepository.findByEmail(requestDTO.getEmail()).isPresent();
        if (alreadyExists) {
            throw new IllegalStateException("이미 셀러 신청을 완료한 회원입니다.");
        }

        // 3. 신청 정보 생성 및 저장 (isApproved = false)
        SellerApplication application = SellerApplication.builder()
                .email(member.getEmail())
                .nickName(member.getNickName())
                .isApproved(false)
                .build();

        sellerApplicationRepository.save(application);

        // 4. DTO 변환 후 반환
        return SellerResponseDTO.builder()
                .id(application.getId())
                .email(application.getEmail())
                .nickName(application.getNickName())
                .isApproved(application.isApproved())
                .statusDescription(application.isApproved() ? "승인 완료" : "승인 대기")
                .build();
    }






}

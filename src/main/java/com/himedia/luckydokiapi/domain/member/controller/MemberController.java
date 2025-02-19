package com.himedia.luckydokiapi.domain.member.controller;


import com.himedia.luckydokiapi.domain.coupon.dto.CouponResponseDto;
import com.himedia.luckydokiapi.domain.coupon.service.CouponService;
import com.himedia.luckydokiapi.domain.member.dto.*;
import com.himedia.luckydokiapi.domain.member.enums.MemberActive;
import com.himedia.luckydokiapi.domain.member.service.MemberService;
import com.himedia.luckydokiapi.props.JwtProps;
import com.himedia.luckydokiapi.security.MemberDTO;
import com.himedia.luckydokiapi.util.CookieUtil;
import com.himedia.luckydokiapi.util.JWTUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.himedia.luckydokiapi.util.TimeUtil.checkTime;

@Slf4j
@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final JWTUtil jwtUtil;
    private final JwtProps jwtProps;
    private final CouponService couponService;

    @PostMapping("/join")
    public ResponseEntity<?> join(@Valid @RequestBody JoinRequestDTO joinRequestDTO) {
        log.info("Join request: {}", joinRequestDTO);
        memberService.join(joinRequestDTO);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody
                                                  LoginRequestDTO loginRequestDTO,
                                                  HttpServletResponse response) {
        log.info("Login request: {}", loginRequestDTO);
        Map<String, Object> loginClaims = memberService.login(loginRequestDTO.getEmail(), loginRequestDTO.getPassword());

        String refreshToken = loginClaims.get("refreshToken").toString();
        String accessToken = loginClaims.get("accessToken").toString();

        CookieUtil.setTokenCookie(response, "refreshToken", refreshToken, jwtProps.getRefreshTokenExpirationPeriod());

/*        // 웹 클라이언트인 경우 쿠키 설정
        if ("web".equals(clientType)) {
            CookieUtil.setTokenCookie(response, "refreshToken", refreshToken, jwtProps.getRefreshTokenExpirationPeriod());
        }
        // 모바일인 경우 헤더로 전송
        else if ("mobile".equals(clientType)) {
            response.setHeader("Refresh-Token", refreshToken);
        }*/

        LoginResponseDTO loginResponseDTO = LoginResponseDTO.builder()
                .email(loginClaims.get("email").toString())
                .nickName(loginClaims.get("nickName").toString())
                .roles((List<String>) loginClaims.get("roleNames"))
                .accessToken(accessToken)
                .active(MemberActive.valueOf(loginClaims.get("active").toString()))
                .build();

        log.info("loginResponseDTO: {}", loginResponseDTO);
        // 로그인 성공시, accessToken, email, name, roles 반환
        return ResponseEntity.ok(loginResponseDTO);
    }


    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        log.info("logout");
        // accessToken은 react 내 redux 상태 지워서 없앰
        // 쿠키 삭제
        CookieUtil.removeTokenCookie(response, "refreshToken");

        return ResponseEntity.ok("logout success!");
    }

//    @GetMapping("/refresh")
//    public Map<String, Object> refresh(
//            @CookieValue(value = "refreshToken") String refreshToken,
//            HttpServletResponse response) {
//        log.info("refresh refreshToken: {}", refreshToken);
//
//        // RefreshToken 검증
//        Map<String, Object> claims = jwtUtil.validateToken(refreshToken);
//        log.info("RefreshToken claims: {}", claims);
//
//        String newAccessToken = jwtUtil.generateToken(claims, jwtProps.getAccessTokenExpirationPeriod());
//        String newRefreshToken = jwtUtil.generateToken(claims, jwtProps.getRefreshTokenExpirationPeriod());
//
//        // refreshToken 만료시간이 1시간 이하로 남았다면, 새로 발급
//        if (checkTime((Integer) claims.get("exp"))) {
//            // 새로 발급
//            CookieUtil.setTokenCookie(response, "refreshToken", newRefreshToken, jwtProps.getRefreshTokenExpirationPeriod()); // 1day
//        } else {
//            // 만료시간이 1시간 이상이면, 기존 refreshToken 그대로
//            CookieUtil.setNewRefreshTokenCookie(response, "refreshToken", refreshToken);
//        }
//
//        return Map.of("newAccessToken", newAccessToken);
//    }


    @PostMapping("/upgrade-to-seller")
    public ResponseEntity<Long> upgradeToSeller(@AuthenticationPrincipal MemberDTO memberDTO, @Valid SellerRequestDTO requestDTO) {
        log.info("셀러 승급 신청 요청 memberDTO: {}, requestDTO: {}", memberDTO, requestDTO);

        return ResponseEntity.ok(memberService.upgradeToSeller(memberDTO.getEmail(), requestDTO));

    }

    @GetMapping("/me")
    public MemberDetailDTO getMyInfo(@AuthenticationPrincipal MemberDTO member) {
        log.info("getMyInfo: {}", member);
        return memberService.getMyInfo(member.getEmail());
    }


    @PutMapping("/me")
    public MemberDetailDTO updateMyInfo(
            @AuthenticationPrincipal MemberDTO member,
            @RequestBody UpdateMemberDTO request) {
        return memberService.updateMyInfo(member.getEmail(), request);
    }

    // 해당 유저의 쿠폰 리스트
    @GetMapping("/coupon/list")
    public List<CouponResponseDto> getUserCoupons(@AuthenticationPrincipal MemberDTO memberDTO) {
        log.info("getUserCoupons memberDTO: {}", memberDTO);
        return couponService.getCouponList(memberDTO.getEmail());
    }

    @DeleteMapping
    public ResponseEntity<String> deleteMember(@AuthenticationPrincipal MemberDTO member) {
        memberService.deleteMember(member.getEmail());
        return ResponseEntity.ok("회원 탈퇴 완료");
    }


}

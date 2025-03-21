package com.himedia.luckydokiapi.domain.member.controller;


import com.himedia.luckydokiapi.domain.coupon.dto.CouponResponseDTO;
import com.himedia.luckydokiapi.domain.coupon.service.CouponService;
import com.himedia.luckydokiapi.domain.member.dto.*;
import com.himedia.luckydokiapi.domain.member.service.MemberService;
import com.himedia.luckydokiapi.domain.notification.dto.FcmTokenRequestDTO;
import com.himedia.luckydokiapi.props.JwtProps;
import com.himedia.luckydokiapi.security.MemberDTO;
import com.himedia.luckydokiapi.security.service.TokenService;
import com.himedia.luckydokiapi.util.CookieUtil;
import com.himedia.luckydokiapi.util.JWTUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.RefreshFailedException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
@Tag(name = "member - api" , description = "로그인 , 회원가입 , 회원정보 조회 / 수정 / 쿠폰 조회 , 셀러 신청 , 알림 권한 등 member 관련 ")
public class MemberController {

    private final MemberService memberService;
    private final JWTUtil jwtUtil;
    private final JwtProps jwtProps;
    private final CouponService couponService;
    private final TokenService tokenService;



    @Operation(summary = "회원가입 및 회원정보 db 에 저장 api")
    @PostMapping("/join")
    public ResponseEntity<?> join(@Valid @RequestBody JoinRequestDTO joinRequestDTO) {
        log.info("Join request: {}", joinRequestDTO);
        memberService.join(joinRequestDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "로그인 , 인증처리 api")
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO loginRequestDTO,
            @RequestParam(required = false, defaultValue = "web") String clientType,
            HttpServletResponse response
    ) {
        log.info("Login request: {}, clientType: {}", loginRequestDTO, clientType);
        // 인증 및 토큰 발급
        // 인증 및 토큰 발급
        LoginResponseDTO loginResponse = memberService.loginToDto(loginRequestDTO.getEmail(), loginRequestDTO.getPassword());

        // 클라이언트 타입에 따라 다른 처리
        if ("web".equals(clientType)) {
            // 웹 클라이언트인 경우 쿠키에 리프레시 토큰 저장
            CookieUtil.setTokenCookie(response, "refreshToken", loginResponse.getRefreshToken(),
                    jwtProps.getRefreshTokenExpirationPeriod());
            // 응답에서는 리프레시 토큰 제거 (보안상 이유)
            loginResponse.setRefreshToken(null);
        }
        // 모바일 클라이언트는 응답 본문에 리프레시 토큰 포함 (이미 포함되어 있음)

        return ResponseEntity.ok(loginResponse);

/*        // 웹 클라이언트인 경우 쿠키 설정
        if ("web".equals(clientType)) {
            CookieUtil.setTokenCookie(response, "refreshToken", refreshToken, jwtProps.getRefreshTokenExpirationPeriod());
        }
        // 모바일인 경우 헤더로 전송
        else if ("mobile".equals(clientType)) {
            response.setHeader("Refresh-Token", refreshToken);
        }*/
    }


    @Operation(summary = "로그아웃 처리")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = false, defaultValue = "web") String clientType,
            @AuthenticationPrincipal MemberDTO memberDTO) {

        log.info("Logout request: {}, clientType: {}", memberDTO, clientType);

        // 액세스 토큰 추출
        String accessToken = extractAccessToken(request);

        if (accessToken != null && memberDTO != null) {
            // Redis에서 토큰 정보 삭제 및 블랙리스트 추가
            tokenService.logout(memberDTO.getEmail(), accessToken);
        }

        // 웹 클라이언트인 경우 쿠키 삭제
        if ("web".equals(clientType)) {
            CookieUtil.removeTokenCookie(response, "refreshToken");
        }

        return ResponseEntity.ok("Logout successful");
    }


    @Operation(summary = "토큰 갱신", description = "refreshToken으로 새로운 accessToken을 발급합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토큰 갱신 성공",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponseDTO.class)) }),
            @ApiResponse(responseCode = "401", description = "인증 실패 - 유효하지 않은 refreshToken"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/refresh")
    public ResponseEntity<LoginResponseDTO> refresh(
            @RequestParam(required = false, defaultValue = "web") String clientType,
            @Parameter(description = "refreshToken 쿠키", required = true)
            @CookieValue(value = "refreshToken", required = false) String cookieRefreshToken,
            @RequestHeader(value = "Refresh-Token", required = false) String headerRefreshToken,
            HttpServletResponse response) throws RefreshFailedException {

        log.info("refresh refreshToken: {} , headerRefreshToken: {}, clientType: {} ", cookieRefreshToken, headerRefreshToken, clientType);

        // 클라이언트 타입에 따라 리프레시 토큰 가져오기
        String refreshToken = "web".equals(clientType) ? cookieRefreshToken : headerRefreshToken;

        // 토큰 갱신 처리
        LoginResponseDTO refreshedTokens = tokenService.refreshTokens(refreshToken);

        // 웹 클라이언트인 경우 새 리프레시 토큰을 쿠키에 저장
        if ("web".equals(clientType)) {
            CookieUtil.setTokenCookie(response, "refreshToken", refreshedTokens.getRefreshToken(),
                    jwtProps.getRefreshTokenExpirationPeriod());
            // 응답에서는 리프레시 토큰 제거
            refreshedTokens.setRefreshToken(null);
        }

        return ResponseEntity.ok(refreshedTokens);
    }

    @Operation(
            summary = "셀러 승급 신청 ",
            description = "일반 회원이 상품을 등록할 수 있는 셀러의 권한을 얻을 수 있는 서비스 입니다 ",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "소개글과 프로필 사진을 업로드 하여 셀러 승급 신청을 할 수 있습니다",
                    required = true,
                    content = @Content(schema = @Schema(implementation = SellerRequestDTO.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "셀러 승급 신청 성공 / 셀러 아이디 반환 "),
            }
    )
    @PostMapping("/upgrade-to-seller")
    public ResponseEntity<Long> upgradeToSeller(@Parameter(description = "인증된 사용자 정보", hidden = true)
                                                    @AuthenticationPrincipal MemberDTO memberDTO, @Valid SellerRequestDTO requestDTO) {
        log.info("셀러 승급 신청 요청 memberDTO: {}, requestDTO: {}", memberDTO, requestDTO);

        return ResponseEntity.ok(memberService.upgradeToSeller(memberDTO.getEmail(), requestDTO));

    }

    @GetMapping("/me")
    public MemberDetailDTO getMyInfo(@AuthenticationPrincipal MemberDTO member) {
        log.info("getMyInfo: {}", member);
        if(member == null) {
            return MemberDetailDTO.builder().build();
        }
        return memberService.getMyInfo(member.getEmail());
    }


    @PutMapping("/me")
    public MemberDetailDTO updateMyInfo(
            @AuthenticationPrincipal MemberDTO member,
            UpdateMemberDTO request) {
        log.info("updateMyInfo request: {}", request);
        return memberService.updateMyInfo(member.getEmail(), request);
    }

    // 해당 유저의 쿠폰 리스트
    @GetMapping("/coupon/list")
    public List<CouponResponseDTO> getUserCoupons(@AuthenticationPrincipal MemberDTO memberDTO) {
        log.info("getUserCoupons memberDTO: {}", memberDTO);
        return couponService.getCouponList(memberDTO.getEmail());
    }

    @DeleteMapping
    public ResponseEntity<String> deleteMember(@AuthenticationPrincipal MemberDTO member) {
        memberService.deleteMember(member.getEmail());
        return ResponseEntity.ok("회원 탈퇴 완료");
    }

    @Operation(summary = "FCM 토큰 업데이트", description = "사용자의 FCM 토큰을 업데이트합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "FCM 토큰 업데이트 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/fcm-token")
    public ResponseEntity<Void> updateFCMToken(
            @Parameter(description = "FCM 토큰 업데이트 요청 데이터", required = true)
            @RequestBody FcmTokenRequestDTO request
    ) {
        log.info("updateFCMToken request: {}", request);
        memberService.updateFCMToken(request.getEmail(), request.getFcmToken());
        return ResponseEntity.ok().build();
    }

    // 회원가입시, 아이디(email) 중복확인 -> false, true 반환
    @GetMapping("/check-email/{email}")
    public ResponseEntity<Boolean> checkEmail(@PathVariable String email) {
        log.info("checkEmail email: {}", email);
        return ResponseEntity.ok(memberService.checkEmail(email));
    }

    /**
     * 요청 헤더에서 액세스 토큰 추출
     */
    private String extractAccessToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

}

package com.himedia.luckydokiapi.domain.member.controller;


import com.himedia.luckydokiapi.domain.chat.dto.ChatRoomDTO;
import com.himedia.luckydokiapi.domain.coupon.dto.CouponResponseDTO;
import com.himedia.luckydokiapi.domain.coupon.service.CouponService;
import com.himedia.luckydokiapi.domain.member.dto.*;
import com.himedia.luckydokiapi.domain.member.enums.MemberActive;
import com.himedia.luckydokiapi.domain.member.service.MemberService;
import com.himedia.luckydokiapi.domain.notification.dto.FcmTokenRequestDTO;
import com.himedia.luckydokiapi.props.JwtProps;
import com.himedia.luckydokiapi.security.MemberDTO;
import com.himedia.luckydokiapi.util.CookieUtil;
import com.himedia.luckydokiapi.util.JWTUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "member - api" , description = "로그인 , 회원가입 , 회원정보 조회 / 수정 / 쿠폰 조회 , 셀러 신청 , 알림 권한 등 member 관련 ")
public class MemberController {

    private final MemberService memberService;
    private final JWTUtil jwtUtil;
    private final JwtProps jwtProps;
    private final CouponService couponService;



    @Operation(summary = "회원가입 및 회원정보 db 에 저장 api")
    @PostMapping("/join")
    public ResponseEntity<?> join(@Valid @RequestBody JoinRequestDTO joinRequestDTO) {
        log.info("Join request: {}", joinRequestDTO);
        memberService.join(joinRequestDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "로그인 , 인증처리 api")
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
            @Parameter(description = "refreshToken 쿠키", required = true)
            @CookieValue(value = "refreshToken") String refreshToken,
            HttpServletResponse response) {
        log.info("refresh refreshToken: {}", refreshToken);

        // ✳️ RefreshToken 검증해서 맴버정보 다시 가져옴!
        Map<String, Object> loginClaims = jwtUtil.validateToken(refreshToken);
        log.info("RefreshToken loginClaims: {}", loginClaims);

        String newAccessToken = jwtUtil.generateToken(loginClaims, jwtProps.getAccessTokenExpirationPeriod());
        String newRefreshToken = jwtUtil.generateToken(loginClaims, jwtProps.getRefreshTokenExpirationPeriod());

        // refreshToken 만료시간이 1시간 이하로 남았다면, 새로 발급
        if (checkTime((Integer) loginClaims.get("exp"))) {
            // 새로 발급
            CookieUtil.setTokenCookie(response, "refreshToken", newRefreshToken, jwtProps.getRefreshTokenExpirationPeriod()); // 1day
        } else {
            // 만료시간이 1시간 이상이면, 기존 refreshToken 그대로
            CookieUtil.setNewRefreshTokenCookie(response, "refreshToken", refreshToken);
        }

        LoginResponseDTO loginResponseDTO = LoginResponseDTO.builder()
                .email(loginClaims.get("email").toString())
                .nickName(loginClaims.get("nickName").toString())
                .roles((List<String>) loginClaims.get("roleNames"))
                .accessToken(newAccessToken)
                .active(MemberActive.valueOf(loginClaims.get("active").toString()))
                .build();

        log.info("refresh loginResponseDTO: {}", loginResponseDTO);
        // refresh 성공시, accessToken, email, name, roles 반환
        return ResponseEntity.ok(loginResponseDTO);
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

}

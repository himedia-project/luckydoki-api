package com.himedia.luckydokiapi.domain.member.controller;



import com.himedia.luckydokiapi.domain.member.dto.*;
import com.himedia.luckydokiapi.domain.member.service.AdminMemberService;
import com.himedia.luckydokiapi.domain.member.service.MemberService;
import com.himedia.luckydokiapi.dto.PageResponseDTO;
import com.himedia.luckydokiapi.props.JwtProps;
import com.himedia.luckydokiapi.util.CookieUtil;
import com.himedia.luckydokiapi.util.JWTUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/admin/member")
@RequiredArgsConstructor
public class AdminMemberController {
    // user - test = 1: N
    private final AdminMemberService adminMemberService;
    private final MemberService memberService;

    private final JWTUtil jwtUtil;
    private final JwtProps jwtProps;


    // join 회원가입
    @PostMapping("/join")
    public ResponseEntity<?> join(@Valid @RequestBody JoinRequestDTO request) {
        log.info("join: {}", request);
        memberService.join(request);
        return ResponseEntity.ok().build();
    }


    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    static class LoginResponseDTO {
        private String email;
        private String nickName;
        private List<String> roles;
        private String accessToken;
    }

    // login security에서 가져옴
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginDTO, HttpServletResponse response) {
        log.info("login: {}", loginDTO);
        Map<String, Object> loginClaims = memberService.login(loginDTO.getEmail(), loginDTO.getPassword());

        // 로그인 성공시 accessToken, refreshToken 생성
        String refreshToken = jwtUtil.generateToken(loginClaims, jwtProps.getRefreshTokenExpirationPeriod());
        String accessToken = loginClaims.get("access_token").toString();
        // TODO: user 로그인시, refreshToken token 테이블에 저장
//        tokenService.saveRefreshToken(accessToken, refreshToken, memberService.getMember(loginDTO.getEmail()));
        // refreshToken 쿠키로 클라이언트에게 전달
        CookieUtil.setTokenCookie(response, "refresh_token", refreshToken, jwtProps.getRefreshTokenExpirationPeriod()); // 1day

        LoginResponseDTO loginResponseDTO = LoginResponseDTO.builder()
                .email(loginClaims.get("email").toString())
                .nickName(loginClaims.get("nickName").toString())
                .roles((List<String>) loginClaims.get("roleNames"))
                .accessToken(accessToken)
                .build();

        log.info("loginResponseDTO: {}", loginResponseDTO);
        // 로그인 성공시, accessToken, email, name, roles 반환
        return ResponseEntity.ok(loginResponseDTO);
    }


    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        log.info("logout");
        // accessToken은 react 내 redux 상태 지워서 없앰
        // 쿠키 삭제
        CookieUtil.removeTokenCookie(response, "refreshToken");

        return ResponseEntity.ok("logout success!");
    }

    @GetMapping("/refresh")
    public Map<String, Object> refresh(
            @CookieValue(value = "refreshToken") String refreshToken,
            HttpServletResponse response) {
        log.info("refresh refreshToken: {}", refreshToken);

        // RefreshToken 검증
        Map<String, Object> claims = jwtUtil.validateToken(refreshToken);
        log.info("RefreshToken claims: {}", claims);

        String newAccessToken = jwtUtil.generateToken(claims, jwtProps.getAccessTokenExpirationPeriod());
        String newRefreshToken = jwtUtil.generateToken(claims, jwtProps.getRefreshTokenExpirationPeriod());

        // refreshToken 만료시간이 1시간 이하로 남았다면, 새로 발급
        if (checkTime((Integer) claims.get("exp"))) {
            // 새로 발급
            CookieUtil.setTokenCookie(response, "refreshToken", newRefreshToken, jwtProps.getRefreshTokenExpirationPeriod()); // 1day
        } else {
            // 만료시간이 1시간 이상이면, 기존 refreshToken 그대로
            CookieUtil.setNewRefreshTokenCookie(response, "refreshToken", refreshToken);
        }

        return Map.of("newAccessToken", newAccessToken);
    }

    // 회원 목록
    @GetMapping("/list")
    public ResponseEntity<PageResponseDTO<MemberResDTO>> user(MemberRequestDTO requestDTO) {
        PageResponseDTO<MemberResDTO> dto = adminMemberService.getList(requestDTO);
        return ResponseEntity.ok(dto);
    }

    // 해당 회원 상세정보
    @GetMapping
    public ResponseEntity<MemberResDTO> getOne(@RequestParam String email) {
        MemberResDTO dto = adminMemberService.getOne(email);
        return ResponseEntity.ok(dto);
    }

    /**
     * 시간이 1시간 미만으로 남았는지 체크
     *
     * @param exp 만료시간
     * @return 1시간 미만이면 true, 아니면 false
     */
    private boolean checkTime(Integer exp) {

        // JWT exp를 날짜로 변환
        Date expDate = new Date((long) exp * 1000);
        // 현재 시간과의 차이 계산 - 밀리세컨즈
        long gap = expDate.getTime() - System.currentTimeMillis();
        // 분단위 계산
        long leftMin = gap / (1000 * 60);
        return leftMin < 60;
    }



}

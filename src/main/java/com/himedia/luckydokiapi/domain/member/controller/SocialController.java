package com.himedia.luckydokiapi.domain.member.controller;

import com.himedia.luckydokiapi.domain.member.dto.LoginResponseDTO;
import com.himedia.luckydokiapi.domain.member.enums.MemberActive;
import com.himedia.luckydokiapi.domain.member.service.MemberService;
import com.himedia.luckydokiapi.domain.member.service.SocialService;
import com.himedia.luckydokiapi.props.JwtProps;
import com.himedia.luckydokiapi.security.MemberDTO;
import com.himedia.luckydokiapi.util.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@Log4j2
@RequiredArgsConstructor
public class SocialController {

    private final MemberService memberService;
    private final SocialService socialService;
    private final JwtProps jwtProps;


    // 카카오 access token 받기
    @GetMapping("/api/member/kakao/token")
    public String getKakaoAccessToken(String code) {
        log.info("getKakaoAccessToken code: {}", code);

        return socialService.getKakaoAccessToken(code);
    }

    // 카카오 로그인 -> 유저정보 받기 + JWT 토큰 발급, cookie에 set
    @GetMapping("/api/member/kakao")
    public ResponseEntity<?> getMemberFromKakao(String accessToken, HttpServletResponse response) {
        log.info("getMemberFromKakao accessToken: {}", accessToken);

        MemberDTO memberDTO = socialService.getKakaoMember(accessToken);
        Map<String, Object> loginClaims = memberService.getSocialClaims(memberDTO);

        return ResponseEntity.ok(this.getSocialLoginResponseDTO(response, loginClaims));
    }


    // 구글 access token 받기
    @GetMapping("/api/member/google/token")
    public String getGoogleAccessToken(String code) {
        log.info("getGoogleAccessToken code: {}", code);

        return socialService.getGoogleAccessToken(code);
    }

    // 구글 로그인 -> 유저정보 받기 + JWT 토큰 발급, cookie에 set
    @GetMapping("/api/member/google")
    public ResponseEntity<?> getMemberFromGoogle(String accessToken, HttpServletResponse response) {
        log.info("getMemberFromGoogle accessToken: {}", accessToken);

        MemberDTO memberDTO = socialService.getGoogleMember(accessToken);
        Map<String, Object> claims = memberService.getSocialClaims(memberDTO);

        return ResponseEntity.ok(this.getSocialLoginResponseDTO(response, claims));

    }

    // 페이스북 access token 받기
    @GetMapping("/api/member/facebook/token")
    public String getFacebookAccessToken(String code) {
        log.info("getFacebookAccessToken code: {}", code);

        return socialService.getFacebookAccessToken(code);
    }

    // 페이스북 로그인 -> 유저정보 받기 + JWT 토큰 발급, cookie에 set
    @GetMapping("/api/member/facebook")
    public Map<String, Object> getMemberFromFacebook(String accessToken, HttpServletResponse response) {
        log.info("getMemberFromFacebook accessToken: {}", accessToken);

        MemberDTO memberDTO = socialService.getFacebookMember(accessToken);
        Map<String, Object> claims = memberService.getSocialClaims(memberDTO);

        CookieUtil.setTokenCookie(response, "accessToken", (String) claims.get("accessToken"), jwtProps.getAccessTokenExpirationPeriod());
        CookieUtil.setTokenCookie(response, "refreshToken", (String) claims.get("refreshToken"), jwtProps.getRefreshTokenExpirationPeriod());

        return claims;

    }

    // 네이버 access token 받기
    @GetMapping("/api/member/naver/token")
    String getNaverAccessToken(String code, String state) {
        log.info("code: " + code);
        log.info("state: " + state);

        return socialService.getNaverAccessToken(code, state);
    }


    // 네이버 로그인 -> 유저정보 받기 + JWT 토큰 발급
    @GetMapping("/api/member/naver")
    public Map<String, Object> getMemberFromNaver(String accessToken, HttpServletResponse response) {
        log.info("getMemberFromNaver accessToken: {}", accessToken);

        MemberDTO memberDTO = socialService.getNaverMember(accessToken);
        Map<String, Object> claims = memberService.getSocialClaims(memberDTO);

        CookieUtil.setTokenCookie(response, "accessToken", (String) claims.get("accessToken"), jwtProps.getAccessTokenExpirationPeriod());
        CookieUtil.setTokenCookie(response, "refreshToken", (String) claims.get("refreshToken"), jwtProps.getRefreshTokenExpirationPeriod());

        return claims;

    }

    // github accesstoken 요청
    @GetMapping("/api/member/github/token")
    public String getGithubAccessToken(String code) {
        log.info("getGithubAccessToken code: {}", code);

        String githubAccessToken = socialService.getGithubAccessToken(code);
        log.info("githubAccessToken: " + githubAccessToken);

        return githubAccessToken;
    }


    // github 사용자 정보 받기
    @GetMapping("/api/member/github")
    public Map<String, Object> getMemberFromGithub(String accessToken, HttpServletResponse response) {
        log.info("getMemberFromGithub accessToken: {}", accessToken);

        MemberDTO memberDTO = socialService.getGithubMember(accessToken);
        Map<String, Object> claims = memberService.getSocialClaims(memberDTO);

        CookieUtil.setTokenCookie(response, "accessToken", (String) claims.get("accessToken"), jwtProps.getAccessTokenExpirationPeriod());
        CookieUtil.setTokenCookie(response, "refreshToken", (String) claims.get("refreshToken"), jwtProps.getRefreshTokenExpirationPeriod());

        return claims;
    }


    /**
     * 소셜 로그인 성공시, refreshToken(in cookie), accessToken, email, name, roles 반환
     *
     * @param loginClaims 로그인 클레임
     * @return 로그인 응답 DTO, refreshToken(in cookie)
     */
    private LoginResponseDTO getSocialLoginResponseDTO(HttpServletResponse response, Map<String, Object> loginClaims) {
        // 로그인 성공시 cookie에 refreshToken 보관해서 반환
        CookieUtil.setTokenCookie(response, "refreshToken", (String) loginClaims.get("refreshToken"), jwtProps.getRefreshTokenExpirationPeriod());

        LoginResponseDTO loginResponseDTO = LoginResponseDTO.builder()
                .email(loginClaims.get("email").toString())
                .nickName(loginClaims.get("nickName").toString())
                .roles((List<String>) loginClaims.get("roleNames"))
                .accessToken((String) loginClaims.get("accessToken"))
                .active(MemberActive.valueOf(loginClaims.get("active").toString()))
                .build();

        log.info("loginResponseDTO: {}", loginResponseDTO);

        return loginResponseDTO;
    }

}

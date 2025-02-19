package com.himedia.luckydokiapi.domain.member.controller;

import com.himedia.luckydokiapi.domain.member.dto.SocialLoginRequestDTO;
import com.himedia.luckydokiapi.domain.member.entity.Member;
import com.himedia.luckydokiapi.domain.member.enums.MemberActive;
import com.himedia.luckydokiapi.domain.member.enums.MemberRole;
import com.himedia.luckydokiapi.domain.member.repository.MemberRepository;
import com.himedia.luckydokiapi.domain.member.service.MemberService;
import com.himedia.luckydokiapi.domain.member.service.SocialService;
import com.himedia.luckydokiapi.props.JwtProps;
import com.himedia.luckydokiapi.security.MemberDTO;
import com.himedia.luckydokiapi.util.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.Map;
import java.util.Optional;

@RestController
@Log4j2
@RequiredArgsConstructor

public class SocialController {

    private final MemberRepository memberRepository;
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
    public Map<String, Object> getMemberFromKakao(String accessToken, HttpServletResponse response) {
        log.info("getMemberFromKakao accessToken: {}", accessToken);

        MemberDTO memberDTO = socialService.getKakaoMember(accessToken);
        Map<String, Object> claims = memberService.getSocialClaims(memberDTO);

        CookieUtil.setTokenCookie(response, "accessToken", (String) claims.get("accessToken"), jwtProps.getAccessTokenExpirationPeriod());
        CookieUtil.setTokenCookie(response, "refreshToken", (String) claims.get("refreshToken"), jwtProps.getRefreshTokenExpirationPeriod());

        return claims;

    }

    // 구글 access token 받기
    @GetMapping("/api/member/google/token")
    public String getGoogleAccessToken(String code) {
        log.info("getGoogleAccessToken code: {}", code);

        return socialService.getGoogleAccessToken(code);
    }

    // 구글 로그인 -> 유저정보 받기 + JWT 토큰 발급, cookie에 set
    @GetMapping("/api/member/google")
    public Map<String, Object> getMemberFromGoogle(String accessToken, HttpServletResponse response) {

        log.info("getMemberFromGoogle accessToken: {}", accessToken);

        MemberDTO memberDTO = socialService.getGoogleMember(accessToken);
        Map<String, Object> claims = memberService.getSocialClaims(memberDTO);

        CookieUtil.setTokenCookie(response, "accessToken", (String) claims.get("accessToken"), jwtProps.getAccessTokenExpirationPeriod());
        CookieUtil.setTokenCookie(response, "refreshToken", (String) claims.get("refreshToken"), jwtProps.getRefreshTokenExpirationPeriod());
        return claims;

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
    @PostMapping("/api/member/social-login")
    public ResponseEntity<?> socialLogin(@RequestBody SocialLoginRequestDTO requestDTO) {
        log.info("소셜 로그인 요청: {}", requestDTO);

        String email = requestDTO.getEmail();
        String nickName = requestDTO.getNickName();

        Optional<Member> existingMemberOpt = memberRepository.findByEmail(email);

        if (existingMemberOpt.isPresent()) {
            Member existingMember = existingMemberOpt.get();

            // ✅ 기존 회원이면 active 값만 유지하고 반환
            if (existingMember.getActive() == MemberActive.N) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("탈퇴한 회원입니다. 다시 가입할 수 없습니다.");
            }

            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("이미 가입된 이메일입니다. 이메일로 로그인해주세요.");
        }

        // ✅ 새 회원 저장 (active = Y 자동 설정)
        Member newMember = Member.builder()
                .email(email)
                .nickName(nickName != null ? nickName : "소셜_사용자") // ✅ 닉네임 기본값 설정
                .active(MemberActive.Y) // ✅ 소셜 로그인 후 active 자동 설정
                .build();

        newMember.addRole(MemberRole.USER);
        memberRepository.save(newMember);

        return ResponseEntity.ok("소셜 로그인 성공, 회원가입 완료");
    }


}

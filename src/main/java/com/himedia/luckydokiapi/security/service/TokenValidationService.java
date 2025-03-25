package com.himedia.luckydokiapi.security.service;

import com.himedia.luckydokiapi.security.CustomUserDetailService;
import com.himedia.luckydokiapi.security.MemberDTO;
import com.himedia.luckydokiapi.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenValidationService {
    private final JWTUtil jwtUtil;
    private final TokenService tokenService;
    private final CustomUserDetailService userDetailService;

    public Authentication validateTokenAndCreateAuthentication(String token) {
        log.info("validateTokenAndCreateAuthentication start... token: {}: ", token);
        if (token == null) {
            throw new RuntimeException("토큰이 없습니다.");
        }

        // 블랙리스트 체크
        if (tokenService.isTokenBlacklisted(token)) {
            throw new RuntimeException("블랙리스트(로그아웃)에 등록된 토큰입니다.");
        }

        // 토큰 검증
        Map<String, Object> claims = jwtUtil.validateToken(token);
        
        // 사용자 정보 로드
        MemberDTO memberDTO = (MemberDTO) userDetailService.loadUserByUsername((String) claims.get("email"));
        
        // 인증 객체 생성
        return new UsernamePasswordAuthenticationToken(
            memberDTO, 
            memberDTO.getPassword(), 
            memberDTO.getAuthorities()
        );
    }
} 
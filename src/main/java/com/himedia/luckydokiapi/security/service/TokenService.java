package com.himedia.luckydokiapi.security.service;

import com.himedia.luckydokiapi.domain.member.dto.LoginResponseDTO;
import com.himedia.luckydokiapi.domain.member.entity.Member;
import com.himedia.luckydokiapi.domain.member.repository.MemberRepository;
import com.himedia.luckydokiapi.props.JwtProps;
import com.himedia.luckydokiapi.security.repository.TokenRepository;
import com.himedia.luckydokiapi.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.security.auth.RefreshFailedException;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenRepository tokenRepository;
    private final JWTUtil jwtUtil;
    private final JwtProps jwtProps;
    private final MemberRepository memberRepository;
    
    /**
     * 로그인 처리 및 토큰 발급
     */
    public LoginResponseDTO issueTokens(Member member) {
        // 토큰에 담을 정보 준비
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", member.getEmail());
        claims.put("nickName", member.getNickName());
        claims.put("active", member.getActive().toString());
        
        List<String> roleNames = member.getMemberRoleList().stream()
                .map(Enum::name)
                .collect(Collectors.toList());
        
        claims.put("roleNames", roleNames);
        
        // 토큰 생성
        String accessToken = jwtUtil.generateToken(claims, jwtProps.getAccessTokenExpirationPeriod());
        String refreshToken = jwtUtil.generateToken(claims, jwtProps.getRefreshTokenExpirationPeriod());
        
        // 리프레시 토큰을 Redis에 저장
        tokenRepository.saveRefreshToken(
                member.getEmail(), 
                refreshToken, 
                jwtProps.getRefreshTokenExpirationPeriod()
        );
        
        // 응답 DTO 생성
        return LoginResponseDTO.builder()
                .email(member.getEmail())
                .nickName(member.getNickName())
                .roles(roleNames)
                .accessToken(accessToken)
                .refreshToken(refreshToken) // 모바일 클라이언트를 위해 refreshToken도 반환 (웹은 쿠키 사용)
                .active(member.getActive())
                .build();
    }
    
    /**
     * 토큰 갱신
     */
    public LoginResponseDTO refreshTokens(String refreshToken) throws RefreshFailedException {
        // 토큰 검증
        if (refreshToken == null) {
            throw new RefreshFailedException("REFRESH_TOKEN_NOT_FOUND");
        }

        Map<String, Object> claims = jwtUtil.validateToken(refreshToken);
        String email = claims.get("email").toString();
        
        // Redis에 저장된 리프레시 토큰과 비교
        String storedRefreshToken = tokenRepository.getRefreshToken(email);
        
        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            throw new RefreshFailedException("REFRESH_TOKEN_NOT_FOUND");
        }
        
        // 토큰 만료 시간 체크
        Integer exp = (Integer) claims.get("exp");
        long expiration = Instant.ofEpochSecond(exp).toEpochMilli();
        long oneHourInMillis = 60 * 60 * 1000;
        boolean shouldRefreshRefreshToken = (expiration - System.currentTimeMillis()) < oneHourInMillis;
        
        // 회원 정보 조회
        Member member = memberRepository.getWithRoles(email)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));
        
        // 새 액세스 토큰 발급
        String newAccessToken = jwtUtil.generateToken(claims, jwtProps.getAccessTokenExpirationPeriod());
        
        // 리프레시 토큰이 1시간 이내로 만료되는 경우 갱신
        String newRefreshToken = refreshToken;
        if (shouldRefreshRefreshToken) {
            newRefreshToken = jwtUtil.generateToken(claims, jwtProps.getRefreshTokenExpirationPeriod());
            tokenRepository.saveRefreshToken(email, newRefreshToken, jwtProps.getRefreshTokenExpirationPeriod());
        }
        
        return LoginResponseDTO.builder()
                .email(member.getEmail())
                .nickName(member.getNickName())
                .roles(member.getMemberRoleList().stream().map(Enum::name).collect(Collectors.toList()))
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .active(member.getActive())
                .build();
    }
    
    /**
     * 로그아웃 처리
     */
    public void logout(String email, String accessToken) {
        // 리프레시 토큰 삭제
        tokenRepository.deleteRefreshToken(email);
        
        // 액세스 토큰 블랙리스트에 추가
        // 만료 시간 계산
        try {
            Map<String, Object> claims = jwtUtil.validateToken(accessToken);
            Integer exp = (Integer) claims.get("exp");
            long expirationTime = Instant.ofEpochSecond(exp).toEpochMilli();
            long remainingTime = expirationTime - System.currentTimeMillis();
            
            if (remainingTime > 0) {
                tokenRepository.addToBlacklist(accessToken, remainingTime);
            }
        } catch (Exception e) {
            log.error("토큰 블랙리스트(로그아웃) 추가 중 오류: {}", e.getMessage());
        }
    }
    
    /**
     * 토큰 블랙리스트 확인
     */
    public boolean isTokenBlacklisted(String accessToken) {
        return tokenRepository.isBlacklisted(accessToken);
    }
} 
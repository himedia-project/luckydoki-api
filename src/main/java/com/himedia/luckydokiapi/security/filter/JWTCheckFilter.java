package com.himedia.luckydokiapi.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.himedia.luckydokiapi.security.CustomUserDetailService;
import com.himedia.luckydokiapi.security.MemberDTO;
import com.himedia.luckydokiapi.security.service.TokenService;
import com.himedia.luckydokiapi.util.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class JWTCheckFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final CustomUserDetailService userDetailService;
    private final TokenService tokenService;

    // 해당 필터로직(doFilterInternal)을 수행할지 여부를 결정하는 메서드
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        log.info("check uri: " + path);

        // Pre-flight 요청은 필터를 타지 않도록 설정
        if (request.getMethod().equals("OPTIONS")) {
            return true;
        }
        // /api/member/로 시작하는 요청은 필터를 타지 않도록 설정
        if (path.startsWith("/api/member/login") || path.startsWith("/api/member/join")
                || path.startsWith("/api/member/check-email")
                || path.startsWith("/api/member/refresh") || path.startsWith("/api/member/logout")
                || path.startsWith("/api/member/kakao") || path.startsWith("/api/member/google")
                || path.startsWith("/api/member/naver") || path.startsWith("/api/member/github")
                || path.startsWith("/api/member/facebook")
                // admin
                || path.startsWith("/api/admin/member/login") || path.startsWith("/api/admin/member/join")
                || path.startsWith("/api/admin/member/refresh") || path.startsWith("/api/admin/member/logout")
                // 회원가입관련 회원 인증
                || path.startsWith("/api/phone/send") || path.startsWith("/api/phone/verify")
        ) {
            return true;
        }
        // "/api/product/list" api는 추가하지 말것!

        if (path.startsWith("/api/admin/product/excel/download")) {
            return true;
        }

        // /view 이미지 불러오기 api로 시작하는 요청은 필터를 타지 않도록 설정
        if (path.startsWith("/api/image/")
        ) {
            return true;
        }

        if (path.startsWith("/api/audio/")
        ) {
            return true;
        }

        if (path.startsWith("/api/event")) {
            return true;
        }


        //websocket handshake 요청 필터  안타게
        if (path.startsWith("/wss-stomp")) {
            return true;
        }


        // python
        if (path.startsWith("/api/sales/forecast")) {
            return true;
        }

        if(path.startsWith("/static")) {
            return true;
        }

        // -----
        // health check
        if (path.startsWith("/health")) {
            return true;
        }

        // Swagger UI 경로 제외 설정
        if (path.startsWith("/swagger-ui/") || path.startsWith("/v3/api-docs")) {
            return true;
        }
        // h2-console 경로 제외 설정
        if (path.startsWith("/h2-console")) {
            return true;
        }

        // /favicon.ico 경로 제외 설정
        if (path.startsWith("/favicon.ico")) {
            return true;
        }

        // 이메일 경로 제외
        if (path.startsWith("/api/email/send")) {
            return true;
        }


        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("------------------JWTCheckFilter.................");
        log.info("request.getServletPath(): {}", request.getServletPath());
        log.info("..................................................");

        String autHeaderStr = request.getHeader("Authorization");
        log.info("autHeaderStr Authorization: {}", autHeaderStr);

        if ((Objects.equals(autHeaderStr, "Bearer null") || (autHeaderStr == null)) && (
                request.getServletPath().startsWith("/api/product/")
                        //리뷰 관련 api 상품 별 리뷰는 필터 안타게
                        || request.getServletPath().matches("^/api/review/list/\\d+$")
                        // shop 관련 api
                        || request.getServletPath().startsWith("/api/shop/")
                        // community 관련 api
                        || request.getServletPath().startsWith("/api/community/")
                        // mypage 관련 api
                        || request.getServletPath().startsWith("/api/member/me")
                        // likes 관련 api
                        || request.getServletPath().startsWith("/api/likes/")
                        // category 관련 api
                        || request.getServletPath().startsWith("/api/category/")
                        // admin dashboard 관련 api
                        || request.getServletPath().startsWith("/api/admin/dashboard")
                        // chat 관련 api
                        || request.getServletPath().startsWith("/api/chat/")
                        // search 관련 api
                        || request.getServletPath().startsWith("/api/search/")


        )) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Bearer accessToken 형태로 전달되므로 Bearer 제거
            String accessToken = autHeaderStr.substring(7);// Bearer 제거
            // 쿠키로 가져와
            log.info("JWTCheckFilter accessToken: {}", accessToken);

            // 블랙리스트(로그아웃된) 토큰 확인
            if (tokenService.isTokenBlacklisted(accessToken)) {
                throw new RuntimeException("블랙리스트(로그아웃)에 등록된 토큰입니다.");
            }

            Map<String, Object> claims = jwtUtil.validateToken(accessToken);

            log.info("JWT claims: {}", claims);

            MemberDTO memberDTO = (MemberDTO) userDetailService.loadUserByUsername((String) claims.get("email"));

            log.info("memberDTO: {}", memberDTO);
            log.info("memberDto.getAuthorities(): {}", memberDTO.getAuthorities());

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(memberDTO, memberDTO.getPassword(), memberDTO.getAuthorities());

            // SecurityContextHolder에 인증 객체 저장
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            // 다음 필터로 이동
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("JWT Check Error...........");
            log.error("e.getMessage(): {}", e.getMessage());

            ObjectMapper objectMapper = new ObjectMapper();
            String msg = objectMapper.writeValueAsString(Map.of("error", "ERROR_ACCESS_TOKEN"));

            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            PrintWriter printWriter = response.getWriter();
            printWriter.println(msg);
            printWriter.close();
        }


    }
}

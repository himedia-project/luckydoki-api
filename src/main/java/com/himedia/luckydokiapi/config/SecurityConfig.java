package com.himedia.luckydokiapi.config;


import com.himedia.luckydokiapi.security.filter.JWTCheckFilter;
import com.himedia.luckydokiapi.security.handler.CustomAccessDeniedHandler;
import com.himedia.luckydokiapi.security.handler.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@EnableWebSecurity  // Spring Security 설정을 활성화
@Configuration
@Log4j2
@RequiredArgsConstructor
@EnableMethodSecurity  // @PreAuthorize, @Secured, @RolesAllowed 어노테이션을 사용하기 위해 필요
public class SecurityConfig {

    private final JWTCheckFilter jwtCheckFilter;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }



    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("security config...............");

//        http
//                .authorizeHttpRequests((authorizedHttpRequests) -> authorizedHttpRequests
//                        .requestMatchers(new AntPathRequestMatcher("/**")).permitAll());
        http.authorizeHttpRequests(
                authorizeHttpRequests -> authorizeHttpRequests
                        .requestMatchers(new AntPathRequestMatcher("/api/email/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/phone/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/shop/**")).permitAll()
                        // /api/admin/join, /api/admin/login,logout 모두 접근 가능
                        .requestMatchers(new AntPathRequestMatcher("/api/member/join")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/member/login")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/member/logout")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/member/refresh")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/member/kakao/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/member/google/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/member/me")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/member/check-email/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/category/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/product/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/content/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/coupon/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/test/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/chat/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/crawl/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/likes/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/community/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/event/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/sales/**")).permitAll()
                        // /api/search/popular-keywords
                        .requestMatchers(new AntPathRequestMatcher("/api/search/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/image/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/audio/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/static/**")).permitAll()
                        // /api/admin/join, /api/admin/login,logout 모두 접근 가능
                        .requestMatchers(new AntPathRequestMatcher("/api/admin/member/join")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/admin/member/login")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/admin/member/logout")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/admin/member/refresh")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/admin/coupon")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/admin/product/excel/download")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/admin/dashboard/**")).permitAll()
                        // health check
                        .requestMatchers(new AntPathRequestMatcher("/health/**")).permitAll()
                        // api path에 admin 포함되면 ROLE_ADMIN 권한이 있어야 접근 가능,
                        .requestMatchers(new AntPathRequestMatcher("/api/admin/**")).hasRole("ADMIN")
                        //review product 별 list 보기 허용
                        .requestMatchers("/api/review/list/*").permitAll()
                        // WebSocket handshake만 허용 ,초기 http 연결이 필요하므로 권한 허용을 해주어야 함( 자세히 보면 엔드포인트가 다릅니다)
                        .requestMatchers("/wss-stomp/**").permitAll()
                        // 정적 리소스에 대한 접근 허용
                        .requestMatchers(new AntPathRequestMatcher("/favicon.ico")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/v2/api-docs")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/swagger-resources/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/swagger-ui/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/webjars/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/v3/api-docs/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()
                        .anyRequest().authenticated()
        );



        http.cors(httpSecurityCorsConfigurer -> {
            httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource());
        });

        http.sessionManagement(sessionConfig -> {
            sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        });

        http.csrf(AbstractHttpConfigurer::disable); // CSRF 보호 기능이 끎 REST API에서는 일반적으로 CSRF 보호가 필요하지 않음


        // h2-console, 해당 페이지가 동일한 출처에서만 프레임으로 로드될 수 있도록 설정
        http
                .headers(headers -> {
                    headers.addHeaderWriter(
                            new XFrameOptionsHeaderWriter(
                                    XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN
                            )
                    )
                            // xss 방지 설정
                            .xssProtection(HeadersConfigurer.XXssConfig::disable)
                            // csp? Content Security Policy : 악성코드 실행을 방지하는 보안 정책 -> 외부 스크립트 실행을 제한하여 xss 공격을 방지
                            .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self'"));

                });

        // JWT Check Filter 추가
        http.addFilterBefore(jwtCheckFilter,
                UsernamePasswordAuthenticationFilter.class);
        // exception authenticationEntryPoint 추가 401 에러 처리
        http.exceptionHandling(exception -> {
            exception.authenticationEntryPoint(customAuthenticationEntryPoint);
        });
        // exceptionHandler, 접근 거부 핸들러 추가
        http.exceptionHandling(config -> {
            config.accessDeniedHandler(customAccessDeniedHandler);
        });

        return http.build();
    }



    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 출처 설정 (모든 출처 허용)
//        configuration.setAllowedOriginPatterns(Arrays.asList("*"));  // localhost:3000 -> 허용
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:3001",
                "http://15.165.150.61","http://15.165.150.61:3000", "http://15.165.150.61:3001", // EC2 퍼블릭 IP 3000, 3001 포트 허용
                "https://openapi.map.naver.com",
                "http://luckydoki.shop:3000", "https://luckydoki.shop:3000",
                "http://www.luckydoki.shop:3000", "https://www.luckydoki.shop:3000",
                "http://luckydoki.shop", "https://luckydoki.shop",
                "http://www.luckydoki.shop", "https://www.luckydoki.shop","https://www.luckydoki.shop/",
                // admin.luckydoki.shop
                "http://admin.luckydoki.shop", "https://admin.luckydoki.shop",
                "http://www.admin.luckydoki.shop", "https://www.admin.luckydoki.shop"
        ));
        // 허용할 메서드 설정
        configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        // 허용할 헤더 설정
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        // 자격 증명 허용 설정 (쿠키 등)
        configuration.setAllowCredentials(true);
        // content-disposition 허용 설정 -> excel 파일 다운로드시, 제목노출을 위해 필요!
        configuration.setExposedHeaders(Arrays.asList("Content-Disposition"));
        // CORS 설정을 특정 경로에 적용
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

}

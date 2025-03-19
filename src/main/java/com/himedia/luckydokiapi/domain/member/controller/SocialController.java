package com.himedia.luckydokiapi.domain.member.controller;

import com.himedia.luckydokiapi.domain.member.dto.LoginResponseDTO;
import com.himedia.luckydokiapi.domain.member.entity.Member;
import com.himedia.luckydokiapi.domain.member.service.MemberService;
import com.himedia.luckydokiapi.domain.member.service.SocialService;
import com.himedia.luckydokiapi.props.JwtProps;
import com.himedia.luckydokiapi.security.MemberDTO;
import com.himedia.luckydokiapi.security.service.TokenService;
import com.himedia.luckydokiapi.util.CookieUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    private final TokenService tokenService;

    @Operation(summary = "카카오 액세스 토큰 획득", description = "카카오 인증 코드를 사용하여 카카오 액세스 토큰을 가져옵니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토큰 획득 성공",
                    content = {@Content(mediaType = "text/plain",
                            schema = @Schema(type = "string"))}),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 인증 코드"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/api/member/kakao/token")
    public String getKakaoAccessToken(@Parameter(description = "카카오 인증 코드", required = true)
                                      String code) {
        log.info("getKakaoAccessToken code: {}", code);
        return socialService.getKakaoAccessToken(code);
    }

    @Operation(summary = "카카오 사용자 정보 조회", description = "카카오 액세스 토큰을 사용하여 사용자 정보를 가져오고 로그인 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 정보 조회 및 로그인 성공",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponseDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 액세스 토큰"),
            @ApiResponse(responseCode = "404", description = "사용자 정보를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/api/member/kakao")
    public ResponseEntity<LoginResponseDTO> getMemberFromKakao(
            String accessToken, 
            HttpServletResponse response,
            @RequestParam(required = false, defaultValue = "web") String clientType) {
        log.info("getMemberFromKakao accessToken: {}, clientType: {}", accessToken, clientType);

        MemberDTO memberDTO = socialService.getKakaoMember(accessToken);
        Member member = memberService.getEntity(memberDTO.getEmail());
        
        return ResponseEntity.ok(processSocialLogin(member, response, clientType));
    }

    @Operation(summary = "구글 액세스 토큰 획득", description = "구글 인증 코드를 사용하여 구글 액세스 토큰을 가져옵니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토큰 획득 성공",
                    content = {@Content(mediaType = "text/plain",
                            schema = @Schema(type = "string"))}),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 인증 코드"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/api/member/google/token")
    public String getGoogleAccessToken(String code) {
        log.info("getGoogleAccessToken code: {}", code);
        return socialService.getGoogleAccessToken(code);
    }

    @Operation(summary = "구글 사용자 정보 조회", description = "구글 액세스 토큰을 사용하여 사용자 정보를 가져오고 로그인 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 정보 조회 및 로그인 성공",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponseDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 액세스 토큰"),
            @ApiResponse(responseCode = "404", description = "사용자 정보를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/api/member/google")
    public ResponseEntity<LoginResponseDTO> getMemberFromGoogle(
            String accessToken, 
            HttpServletResponse response,
            @RequestParam(required = false, defaultValue = "web") String clientType) {
        log.info("getMemberFromGoogle accessToken: {}, clientType: {}", accessToken, clientType);

        MemberDTO memberDTO = socialService.getGoogleMember(accessToken);
        Member member = memberService.getEntity(memberDTO.getEmail());
        
        return ResponseEntity.ok(processSocialLogin(member, response, clientType));
    }

    @Operation(summary = "페이스북 액세스 토큰 획득", description = "페이스북 인증 코드를 사용하여 페이스북 액세스 토큰을 가져옵니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토큰 획득 성공",
                    content = {@Content(mediaType = "text/plain",
                            schema = @Schema(type = "string"))}),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 인증 코드"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/api/member/facebook/token")
    public String getFacebookAccessToken(String code) {
        log.info("getFacebookAccessToken code: {}", code);
        return socialService.getFacebookAccessToken(code);
    }

    @Operation(summary = "페이스북 사용자 정보 조회", description = "페이스북 액세스 토큰을 사용하여 사용자 정보를 가져오고 로그인 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 정보 조회 및 로그인 성공",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponseDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 액세스 토큰"),
            @ApiResponse(responseCode = "404", description = "사용자 정보를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/api/member/facebook")
    public ResponseEntity<LoginResponseDTO> getMemberFromFacebook(
            String accessToken, 
            HttpServletResponse response,
            @RequestParam(required = false, defaultValue = "web") String clientType) {
        log.info("getMemberFromFacebook accessToken: {}, clientType: {}", accessToken, clientType);

        MemberDTO memberDTO = socialService.getFacebookMember(accessToken);
        Member member = memberService.getEntity(memberDTO.getEmail());
        
        return ResponseEntity.ok(processSocialLogin(member, response, clientType));
    }

    @Operation(summary = "네이버 액세스 토큰 획득", description = "네이버 인증 코드를 사용하여 네이버 액세스 토큰을 가져옵니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토큰 획득 성공",
                    content = {@Content(mediaType = "text/plain",
                            schema = @Schema(type = "string"))}),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 인증 코드"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/api/member/naver/token")
    String getNaverAccessToken(String code, String state) {
        log.info("code: " + code);
        log.info("state: " + state);
        return socialService.getNaverAccessToken(code, state);
    }

    @Operation(summary = "네이버 사용자 정보 조회", description = "네이버 액세스 토큰을 사용하여 사용자 정보를 가져오고 로그인 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 정보 조회 및 로그인 성공",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponseDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 액세스 토큰"),
            @ApiResponse(responseCode = "404", description = "사용자 정보를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/api/member/naver")
    public ResponseEntity<LoginResponseDTO> getMemberFromNaver(
            String accessToken, 
            HttpServletResponse response,
            @RequestParam(required = false, defaultValue = "web") String clientType) {
        log.info("getMemberFromNaver accessToken: {}, clientType: {}", accessToken, clientType);

        MemberDTO memberDTO = socialService.getNaverMember(accessToken);
        Member member = memberService.getEntity(memberDTO.getEmail());
        
        return ResponseEntity.ok(processSocialLogin(member, response, clientType));
    }

    @Operation(summary = "깃허브 액세스 토큰 요청", description = "깃허브 인증 코드를 사용하여 깃허브 액세스 토큰을 가져옵니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토큰 획득 성공",
                    content = {@Content(mediaType = "text/plain",
                            schema = @Schema(type = "string"))}),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 인증 코드"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/api/member/github/token")
    public String getGithubAccessToken(String code) {
        log.info("getGithubAccessToken code: {}", code);
        String githubAccessToken = socialService.getGithubAccessToken(code);
        log.info("githubAccessToken: " + githubAccessToken);
        return githubAccessToken;
    }

    @Operation(summary = "깃허브 사용자 정보 조회", description = "깃허브 액세스 토큰을 사용하여 사용자 정보를 가져오고 로그인 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 정보 조회 및 로그인 성공",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponseDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 액세스 토큰"),
            @ApiResponse(responseCode = "404", description = "사용자 정보를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/api/member/github")
    public ResponseEntity<LoginResponseDTO> getMemberFromGithub(
            String accessToken, 
            HttpServletResponse response,
            @RequestParam(required = false, defaultValue = "web") String clientType) {
        log.info("getMemberFromGithub accessToken: {}, clientType: {}", accessToken, clientType);

        MemberDTO memberDTO = socialService.getGithubMember(accessToken);
        Member member = memberService.getEntity(memberDTO.getEmail());
        
        return ResponseEntity.ok(processSocialLogin(member, response, clientType));
    }

    /**
     * 소셜 로그인 공통 처리 로직
     * TokenService를 사용하여 Redis에 토큰을 저장하고 응답 생성
     */
    private LoginResponseDTO processSocialLogin(Member member, HttpServletResponse response, String clientType) {
        // TokenService를 사용하여 토큰 발급 및 Redis에 저장
        LoginResponseDTO loginResponse = tokenService.issueTokens(member);
        log.info("소셜 로그인 처리 완료, 사용자: {}, 클라이언트 타입: {}", member.getEmail(), clientType);
        
        // 웹 클라이언트인 경우 쿠키에 리프레시 토큰 저장
        if ("web".equals(clientType)) {
            CookieUtil.setTokenCookie(response, "refreshToken", loginResponse.getRefreshToken(), 
                    jwtProps.getRefreshTokenExpirationPeriod());
            // 응답에서는 리프레시 토큰 제거 (보안상 이유)
            loginResponse.setRefreshToken(null);
        }
        // 모바일 클라이언트는 응답 본문에 리프레시 토큰 포함 (이미 포함되어 있음)
        
        return loginResponse;
    }


/*    private LoginResponseDTO getSocialLoginResponseDTO(HttpServletResponse response, Map<String, Object> loginClaims) {
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
    }*/
}

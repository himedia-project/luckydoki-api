package com.himedia.luckydokiapi.domain.member.controller;


import com.himedia.luckydokiapi.domain.member.dto.*;
import com.himedia.luckydokiapi.domain.member.service.MemberService;
import com.himedia.luckydokiapi.props.JwtProps;
import com.himedia.luckydokiapi.security.MemberDTO;
import com.himedia.luckydokiapi.util.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final JwtProps jwtProps;


    @PostMapping("/join")
    public ResponseEntity<?> join(@Valid @RequestBody JoinRequestDTO joinRequestDTO) {
        log.info("Join request: {}", joinRequestDTO);
        memberService.join(joinRequestDTO);
        return ResponseEntity.ok().build();
    }


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



    @PostMapping("/upgrade-to-seller")
    public ResponseEntity<Long> upgradeToSeller(@AuthenticationPrincipal MemberDTO memberDTO, @Valid SellerRequestDTO requestDTO) {
        log.info("셀러 승급 신청 요청 memberDTO: {}, requestDTO: {}", memberDTO, requestDTO);

        return ResponseEntity.ok(memberService.upgradeToSeller(memberDTO.getEmail(), requestDTO));

    }

    @GetMapping("/me")
    public MemberDTO getMyInfo(@AuthenticationPrincipal MemberDTO member) {
        return memberService.getMyInfo(member.getEmail());
    }


    @PutMapping("/me")
    public MemberDTO updateMyInfo(
            @AuthenticationPrincipal MemberDTO member,
            @RequestBody UpdateMemberDTO request) {
        return memberService.updateMyInfo(member.getEmail(), request);
    }


}
//true -> adult , false -> 잼민이 ㅋ
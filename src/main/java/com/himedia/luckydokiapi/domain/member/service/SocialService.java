package com.himedia.luckydokiapi.domain.member.service;

import com.himedia.luckydokiapi.domain.member.entity.Member;
import com.himedia.luckydokiapi.domain.member.enums.MemberRole;
import com.himedia.luckydokiapi.domain.member.repository.MemberRepository;
import com.himedia.luckydokiapi.props.SocialProps;
import com.himedia.luckydokiapi.security.MemberDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;


import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SocialService {

    private final RestTemplate restTemplate;

    // @Value 어노테이션을 사용하면 final 키워드를 사용할 수 없다.
    // 생성자 주입방식 말고 필드 주입방식으로 객체주입

    private final SocialProps socialProps;
    private final MemberRepository memberRepository;

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    
    public String getKakaoAccessToken(String code) {
        log.info("getKakaoAccessToken start...");

        // 문자열로 반환할려면?
        String kakaoTokenURL = socialProps.getKakao().getTokenUri();

        String clientID = socialProps.getKakao().getClientId();
        String clientSecret = socialProps.getKakao().getClientSecret();
        String redirectURI = socialProps.getKakao().getRedirectUri();


        HttpHeaders headers = new HttpHeaders();

        headers.add("Content-Type", "application/x-www-form-urlencoded");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        UriComponents uriBuilder = UriComponentsBuilder.fromHttpUrl(kakaoTokenURL)
                .queryParam("grant_type", "authorization_code")
                .queryParam("client_id", clientID)
                .queryParam("client_secret", clientSecret)
                .queryParam("redirect_uri", redirectURI)
                .queryParam("code", code)
                .build();

        ResponseEntity<LinkedHashMap> response =
                restTemplate.exchange(uriBuilder.toString(), HttpMethod.POST, entity, LinkedHashMap.class);

        log.info("response: {}", response);

        LinkedHashMap<String, String> bodyMap = response.getBody();

        log.info("bodyMap: {}", bodyMap);

        return bodyMap.get("access_token");
    }


    @Transactional
    public MemberDTO getKakaoMember(String accessToken) {
        log.info("getKakaoMember start...");
        String email = getEmailFromKakaoAccessToken(accessToken);
        log.info("getKakaoMember email: {}", email);

        Optional<Member> result = memberRepository.findById(email);

        // 기존의 회원
        if (result.isPresent()) {
            MemberDTO memberDTO = memberService.entityToDTO(result.get());
            return memberDTO;
        }

        // 회원이 아니었다면 닉네임은 '소셜 회원'으로 패스워드는 임의로 생성
        Member socialMember = this.makeSocialMember(email);
        memberRepository.save(socialMember);
        MemberDTO memberDTO = memberService.entityToDTO(socialMember);

        return memberDTO;
    }


    
    public String getGoogleAccessToken(String code) {
        log.info("getGoogleAccessToken start...");

        String googleTokenURL = socialProps.getGoogle().getTokenUri();

        String clientID = socialProps.getGoogle().getClientId();

        String clientSecret = socialProps.getGoogle().getClientSecret();

        String redirectURI = socialProps.getGoogle().getRedirectUri();


        HttpHeaders headers = new HttpHeaders();

        headers.add("Content-Type", "application/x-www-form-urlencoded");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        UriComponents uriBuilder = UriComponentsBuilder.fromHttpUrl(googleTokenURL)
                .queryParam("code", code)
                .queryParam("client_id", clientID)
                .queryParam("client_secret", clientSecret)
                .queryParam("redirect_uri", redirectURI)
                .queryParam("grant_type", "authorization_code")
                .build();

        ResponseEntity<LinkedHashMap> response =
                restTemplate.exchange(uriBuilder.toString(), HttpMethod.POST, entity, LinkedHashMap.class);

        log.info("response: {}", response);

        LinkedHashMap<String, String> bodyMap = response.getBody();

        log.info("bodyMap: {}", bodyMap);
        return bodyMap.get("access_token");
    }


    @Transactional
    public MemberDTO getGoogleMember(String accessToken) {
        log.info("getGoogleMember start...");
        String email = getEmailFromGoogleAccessToken(accessToken);
        log.info("getGoogleMember email: {}", email);

        Optional<Member> result = memberRepository.findById(email);

        // 기존의 회원
        if (result.isPresent()) {
            MemberDTO memberDTO = memberService.entityToDTO(result.get());
            return memberDTO;
        }

        // 회원이 아니었다면 닉네임은 '소셜 회원'으로 패스워드는 임의로 생성
        Member socialMember = this.makeSocialMember(email);
        memberRepository.save(socialMember);
        MemberDTO memberDTO = memberService.entityToDTO(socialMember);
        return memberDTO;
    }


    
    public String getFacebookAccessToken(String code) {
        log.info("getFacebookAccessToken start...");

        String facebookTokenURL = socialProps.getFacebook().getTokenUri();

        String clientID = socialProps.getFacebook().getClientId();

        String clientSecret = socialProps.getFacebook().getClientSecret();

        String redirectURI = socialProps.getFacebook().getRedirectUri();


        HttpHeaders headers = new HttpHeaders();

        headers.add("Content-Type", "application/x-www-form-urlencoded");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        UriComponents uriBuilder = UriComponentsBuilder.fromHttpUrl(facebookTokenURL)
                .queryParam("client_id", clientID)
                .queryParam("client_secret", clientSecret)
                .queryParam("redirect_uri", redirectURI)
                .queryParam("code", code)
                .build();

        ResponseEntity<LinkedHashMap> response =
                restTemplate.exchange(uriBuilder.toString(), HttpMethod.GET, entity, LinkedHashMap.class);

        log.info("response: {}", response);

        LinkedHashMap<String, String> bodyMap = response.getBody();

        log.info("bodyMap: {}", bodyMap);
        return bodyMap.get("access_token");
    }


    @Transactional
    public MemberDTO getFacebookMember(String accessToken) {
        log.info("getFacebookMember start...");
        // 페이스북은 이메일을 바로 제공해주기 때문에 바로 사용
        String email = getEmailFromFacebookAccessToken(accessToken);
        log.info("getFacebookMember email: {}", email);
        Optional<Member> result = memberRepository.findById(email);

        // 기존의 회원
        if (result.isPresent()) {
            MemberDTO memberDTO = memberService.entityToDTO(result.get());
            return memberDTO;
        }

        // 회원이 아니었다면 닉네임은 '소셜 회원'으로 패스워드는 임의로 생성
        Member socialMember = this.makeSocialMember(email);
        memberRepository.save(socialMember);
        MemberDTO memberDTO = memberService.entityToDTO(socialMember);
        return memberDTO;
    }


    
    public String getNaverAccessToken(String code, String state) {
        log.info("getNaverAccessToken start...");

        // 네이버는
        String naverTokenURL = socialProps.getNaver().getTokenUri();
        String clientID = socialProps.getNaver().getClientId();
        String clientSecret = socialProps.getNaver().getClientSecret();


        HttpHeaders headers = new HttpHeaders();

        headers.add("Content-Type", "application/x-www-form-urlencoded");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        UriComponents uriBuilder = UriComponentsBuilder.fromHttpUrl(naverTokenURL)
                .queryParam("grant_type", "authorization_code")
                .queryParam("client_id", clientID)
                .queryParam("client_secret", clientSecret)
                .queryParam("code", code)
                .queryParam("state", state)
                .build();

        ResponseEntity<LinkedHashMap> response = restTemplate.exchange(uriBuilder.toString(), HttpMethod.POST, entity, LinkedHashMap.class);

        log.info("response: {}", response);

        LinkedHashMap<String, String> bodyMap = response.getBody();

        log.info("bodyMap: {}", bodyMap);
        return bodyMap.get("access_token");
    }

    @Transactional
    public MemberDTO getNaverMember(String accessToken) {
        log.info("getNaverMember start...");
        // 네이버는 이메일을 바로 제공해주기 때문에 바로 사용
        String email = getEmailFromNaverAccessToken(accessToken);
        log.info("getNaverMember email: {}", email);

        Optional<Member> result = memberRepository.findById(email);

        // 기존의 회원
        if (result.isPresent()) {
            MemberDTO memberDTO = memberService.entityToDTO(result.get());
            return memberDTO;
        }

        // 회원이 아니었다면 닉네임은 '소셜 회원'으로 패스워드는 임의로 생성
        Member socialMember = this.makeSocialMember(email);
        memberRepository.save(socialMember);
        MemberDTO memberDTO = memberService.entityToDTO(socialMember);
        return memberDTO;
    }


    
    public String getGithubAccessToken(String code) {
        log.info("getGithubAccessToken start...");

        String githubTokenURL = socialProps.getGithub().getTokenUri();

        String clientID = socialProps.getGithub().getClientId();
        String clientSecret = socialProps.getGithub().getClientSecret();


        HttpHeaders headers = new HttpHeaders();

        headers.add("Content-Type", "application/x-www-form-urlencoded");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        UriComponents uriBuilder = UriComponentsBuilder.fromHttpUrl(githubTokenURL)
                .queryParam("client_id", clientID)
                .queryParam("client_secret", clientSecret)
                .queryParam("code", code)
                .build();

        ResponseEntity<String> response = restTemplate.exchange(uriBuilder.toString(), HttpMethod.POST, entity, String.class);

        log.info("response: {}", response);

        String body = response.getBody();

        log.info("body: {}", body);

        return body.split("&")[0].split("=")[1];
    }


    @Transactional
    public MemberDTO getGithubMember(String accessToken) {

        log.info("getGithubMember start...");

        String email = getEmailFromGithubAccessToken(accessToken);
        log.info("getGithubMember email: {}", email);

        Optional<Member> result = memberRepository.findById(email);

        // 기존의 회원
        if (result.isPresent()) {
            MemberDTO memberDTO = memberService.entityToDTO(result.get());
            return memberDTO;
        }

        // 회원이 아니었다면 닉네임은 '소셜 회원'으로 패스워드는 임의로 생성
        Member socialMember = this.makeSocialMember(email);
        memberRepository.save(socialMember);
        MemberDTO memberDTO = memberService.entityToDTO(socialMember);
        return memberDTO;
    }


    private String getEmailFromKakaoAccessToken(String accessToken) {

        log.info("getEmailFromKakaoAccessToken start...");
        String kakaoGetUserURL = socialProps.getKakao().getUserInfoUri();

        if (accessToken == null) {
            throw new RuntimeException("Access Token is null");
        }


        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-Type", "application/x-www-form-urlencoded");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        UriComponents uriBuilder = UriComponentsBuilder.fromHttpUrl(kakaoGetUserURL).build();

        ResponseEntity<LinkedHashMap> response
                = restTemplate.exchange(uriBuilder.toString(), HttpMethod.GET, entity, LinkedHashMap.class);

        log.info("response: {}", response);

        LinkedHashMap<String, LinkedHashMap> bodyMap = response.getBody();
        log.info("bodyMap: {}", bodyMap);

        LinkedHashMap<String, String> kakaoAccount = bodyMap.get("kakao_account");
        log.info("kakaoAccount: {}", kakaoAccount);

        return kakaoAccount.get("email");
    }

    private String getEmailFromGoogleAccessToken(String accessToken) {
        log.info("getEmailFromGoogleAccessToken start...");
        // 리소스 uri
        String googleGetUserURL = socialProps.getGoogle().getUserInfoUri();

        if (accessToken == null) {
            throw new RuntimeException("Access Token is null");
        }


        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-Type", "application/x-www-form-urlencoded");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        UriComponents uriBuilder = UriComponentsBuilder.fromHttpUrl(googleGetUserURL).build();

        ResponseEntity<LinkedHashMap> response
                = restTemplate.exchange(uriBuilder.toString(), HttpMethod.GET, entity, LinkedHashMap.class);

        log.info("response: {}", response);

        LinkedHashMap<String, String> bodyMap = response.getBody();

        log.info("bodyMap: {}", bodyMap);

        return bodyMap.get("email");
    }

    private String getEmailFromFacebookAccessToken(String accessToken) {
        log.info("getEmailFromFacebookAccessToken start...");
        // 페이스북은 이메일을 바로 제공해주기 때문에 바로 사용, fields=email 로 꼭 지정해야
        // 리소스 uri
        String facebookGetUserURL = socialProps.getFacebook().getUserInfoUri() + "?fields=email";

        if (accessToken == null) {
            throw new RuntimeException("Access Token is null");
        }


        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-Type", "application/x-www-form-urlencoded");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        UriComponents uriBuilder = UriComponentsBuilder.fromHttpUrl(facebookGetUserURL).build();

        ResponseEntity<LinkedHashMap> response =
                restTemplate.exchange(uriBuilder.toString(), HttpMethod.GET, entity, LinkedHashMap.class);

        log.info("response: {}", response);

        LinkedHashMap<String, String> bodyMap = response.getBody();

        log.info("bodyMap: {}", bodyMap);

        return bodyMap.get("email");
    }

    private String getEmailFromNaverAccessToken(String accessToken) {
        log.info("getEmailFromNaverAccessToken start...");

        String naverGetUserURL = socialProps.getNaver().getUserInfoUri();

        if (accessToken == null) {
            throw new RuntimeException("Access Token is null");
        }


        HttpHeaders headers = new HttpHeaders();

        headers.add("Authorization", "Bearer " + accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        UriComponents uriBuilder = UriComponentsBuilder.fromHttpUrl(naverGetUserURL).build();

        ResponseEntity<LinkedHashMap> response =
                restTemplate.exchange(uriBuilder.toString(), HttpMethod.GET, entity, LinkedHashMap.class);

        log.info("response: {}", response);

        LinkedHashMap<String, LinkedHashMap> bodyMap = response.getBody();

        log.info("bodyMap: {}", bodyMap);


        return bodyMap.get("response").get("email").toString();
    }


    private String getEmailFromGithubAccessToken(String accessToken) {
        log.info("getEmailFromGithubAccessToken start...");

        // email을 얻기 위해서는 별도의 요청을 해야 한다.
        String githubGetUserURL = socialProps.getGithub().getUserInfoUri() + "/emails";

        if (accessToken == null) {
            throw new RuntimeException("Access Token is null");
        }


        HttpHeaders headers = new HttpHeaders();

        headers.add("Authorization", "Bearer " + accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        UriComponents uriBuilder = UriComponentsBuilder.fromHttpUrl(githubGetUserURL).build();

        ResponseEntity<List<LinkedHashMap>> response =
                restTemplate.exchange(uriBuilder.toString(), HttpMethod.GET, entity, new ParameterizedTypeReference<List<LinkedHashMap>>() {
                });

        log.info("response: {}", response);
        // response: <200 OK OK,[{email=mooh2jj@naver.com, primary=true, verified=true, visibility=private}, {email=62453668+mooh2jj@users.noreply.github.com, primary=false, verified=true, visibility=null}],[Date:"Fri, 11 Oct 2024 02:13:05 GMT", Content-Type:"application/json; charset=utf-8", Content-Length:"189", Cache-Control:"private, max-age=60, s-maxage=60", Vary:"Accept, Authorization, Cookie, X-GitHub-OTP,Accept-Encoding, Accept, X-Requested-With", ETag:""dd3a106e7cbae32bdb0061a7ba90516a9fab101b4b135f21a393f59efb6f4bbc"", X-OAuth-Scopes:"user:email", X-Accepted-OAuth-Scopes:"user, user:email", x-oauth-client-id:"Ov23li5aPxNRlQkUulcv", X-GitHub-Media-Type:"github.v3", x-github-api-version-selected:"2022-11-28", X-RateLimit-Limit:"5000", X-RateLimit-Remaining:"4977", X-RateLimit-Reset:"1728614559", X-RateLimit-Used:"23", X-RateLimit-Resource:"core", Access-Control-Expose-Headers:"ETag, Link, Location, Retry-After, X-GitHub-OTP, X-RateLimit-Limit, X-RateLimit-Remaining, X-RateLimit-Used, X-RateLimit-Resource, X-RateLimit-Reset, X-OAuth-Scopes, X-Accepted-OAuth-Scopes, X-Poll-Interval, X-GitHub-Media-Type, X-GitHub-SSO, X-GitHub-Request-Id, Deprecation, Sunset", Access-Control-Allow-Origin:"*", Strict-Transport-Security:"max-age=31536000; includeSubdomains; preload", X-Frame-Options:"deny", X-Content-Type-Options:"nosniff", X-XSS-Protection:"0", Referrer-Policy:"origin-when-cross-origin, strict-origin-when-cross-origin", Content-Security-Policy:"default-src 'none'", Server:"github.com", X-GitHub-Request-Id:"31D5:34B816:3E082B:440B04:670889B1"]>

        // email을 찾아서 리턴
        List<LinkedHashMap> bodyMapList = response.getBody();

        String userEmail = "";

        for (LinkedHashMap map : bodyMapList) {
            if ((boolean) map.get("primary")) {
                userEmail = map.get("email").toString();
            }
        }

        return userEmail;
    }

    
    public Member makeSocialMember(String email) {
        String tempPassword = memberService.makeTempPassword();
        log.info("tempPassword: " + tempPassword);
        String nickname = "소셜회원";
        Member member = Member.builder()
                .email(email)
                .password(passwordEncoder.encode(tempPassword))
                .name(nickname)
                .build();
        member.addRole(MemberRole.USER);
        return member;
    }
}

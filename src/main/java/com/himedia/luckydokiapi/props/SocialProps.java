package com.himedia.luckydokiapi.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 *  소셜 로그인 정보를 담는 클래스
 */

@Data
@Component
@ConfigurationProperties("app.props.social")
public class SocialProps {
    private SocialInfo kakao;
    private SocialInfo naver;
    private SocialInfo google;
    private SocialInfo facebook;
    private SocialInfo github;

    @Data
    public static class SocialInfo {
        private String clientId;
        private String clientSecret;
        private String redirectUri;
        private String authorizationUri;
        private String tokenUri;
        private String userInfoUri;
        private String userInfoNameAttributeKey;
        private String clientName;
    }

}

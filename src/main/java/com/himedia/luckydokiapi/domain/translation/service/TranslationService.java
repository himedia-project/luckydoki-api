package com.himedia.luckydokiapi.domain.translation.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class TranslationService {


    @Value("classpath:google-translation-account.json")
    private Resource resource;

    /**
     * 한국어 텍스트를 영어로 번역
     * @param koreanText 번역할 한국어 텍스트
     * @return 번역된 영어 텍스트
     */
    public String translateToEnglish(String koreanText) {
        log.info("resource: {}", resource);
        if (koreanText == null || koreanText.trim().isEmpty()) {
            return "";
        }
        
        try {
            // Google Cloud 서비스 계정 JSON 파일로부터 인증 정보 로드
            GoogleCredentials credentials = GoogleCredentials.fromStream(resource.getInputStream());
            
            // Google Cloud Translation API 클라이언트 인스턴스 생성
            Translate translate = TranslateOptions.newBuilder()
                    .setCredentials(credentials)
                    .build()
                    .getService();
            
            // 번역 요청 (한국어 -> 영어)
            Translation translation = translate.translate(
                    koreanText,
                    Translate.TranslateOption.sourceLanguage("ko"),
                    Translate.TranslateOption.targetLanguage("en")
            );
            
            return translation.getTranslatedText();
        } catch (IOException e) {
            log.error("서비스 계정 JSON 파일을 불러오는 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("번역 서비스 인증 실패", e);
        } catch (Exception e) {
            log.error("번역 처리 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("번역 실패", e);
        }
    }
} 
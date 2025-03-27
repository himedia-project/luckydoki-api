package com.himedia.luckydokiapi.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.himedia.luckydokiapi.props.FcmProps;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Configuration
public class FirebaseConfig {

    /**
     * Firebase Admin SDK JSON 파일 경로
     */
    @Value("classpath:/firebase-service-account.json")
    private Resource firebaseConfigFilePath;

    @PostConstruct
    public void initialize() {
        try {
            InputStream serviceAccount = firebaseConfigFilePath.getInputStream();
            
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                log.info("Firebase application has been initialized");
            }
        } catch (IOException e) {
            log.error("Firebase 초기화 중 에러 발생: ", e);
            throw new RuntimeException("Firebase 초기화 실패", e);
        }
    }
} 
package com.himedia.luckydokiapi.util.file;

import lombok.extern.slf4j.Slf4j;

import java.util.Set;

/**
 * 파일명 관련 유틸리티 클래스
 */
@Slf4j
public class FileNameUtil {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("svg", "jpg", "jpeg", "png", "gif", "webp");

    private FileNameUtil() {
        // 유틸 클래스이므로 인스턴스 생성 방지
    }

    /**
     * 파일 확장자 추출
     */
    public static String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            throw new IllegalArgumentException("파일 확장자를 찾을 수 없습니다.");
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * 파일명에서 확장자를 제외한 기본 이름 추출
     */
    public static String getBaseFileName(String filename) {
        if (filename == null || !filename.contains(".")) {
            return filename;
        }
        return filename.substring(0, filename.lastIndexOf("."));
    }

    /**
     * 파일명을 안전한 형태로 정규화 (한글 및 특수문자 제거)
     * HTTP 헤더 호환성을 위해 ASCII 문자만 허용
     */
    public static String sanitizeFileName(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return "unnamed_file";
        }
        
        // 1. 확장자 분리
        String name = filename;
        String extension = "";
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex > 0) {
            name = filename.substring(0, lastDotIndex);
            extension = filename.substring(lastDotIndex);
        }
        
        // 2. 한글 및 특수문자 제거/변환
        String sanitizedName = name
                .replaceAll("[가-힣]", "")           // 한글 제거
                .replaceAll("[^a-zA-Z0-9._-]", "_")  // 영문, 숫자, 점, 언더스코어, 하이픈만 허용
                .replaceAll("_{2,}", "_")            // 연속된 언더스코어를 하나로 변환
                .replaceAll("^_+|_+$", "");         // 앞뒤 언더스코어 제거
        
        // 3. 빈 문자열인 경우 기본값 사용
        if (sanitizedName.isEmpty()) {
            sanitizedName = "file";
        }
        
        // 4. 길이 제한 (최대 50자)
        if (sanitizedName.length() > 50) {
            sanitizedName = sanitizedName.substring(0, 50);
        }
        
        return sanitizedName + extension;
    }

    /**
     * HTTP 헤더에 안전한 파일명 생성 (한글 및 특수문자 제거)
     */
    public static String sanitizeFileNameForHeader(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return "file";
        }
        
        // 파일명에서 한글 및 특수문자 제거
        String sanitized = fileName
                .replaceAll("[가-힣]", "")           // 한글 제거
                .replaceAll("[^a-zA-Z0-9._-]", "_")  // 영문, 숫자, 점, 언더스코어, 하이픈만 허용
                .replaceAll("_{2,}", "_")            // 연속된 언더스코어를 하나로 변환
                .replaceAll("^_+|_+$", "");         // 앞뒤 언더스코어 제거
        
        // 빈 문자열인 경우 기본값 사용
        if (sanitized.isEmpty()) {
            sanitized = "file";
        }
        
        // 길이 제한 (최대 100자)
        if (sanitized.length() > 100) {
            sanitized = sanitized.substring(0, 100);
        }
        
        return sanitized;
    }

    /**
     * 파일 확장자 유효성 검증
     */
    public static boolean isValidImageExtension(String extension) {
        return ALLOWED_EXTENSIONS.contains(extension.toLowerCase());
    }

    /**
     * 파일 확장자에 따른 Content-Type 반환
     */
    public static String getContentType(String extension) {
        return switch (extension.toLowerCase()) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "webp" -> "image/webp";
            case "svg" -> "image/svg+xml";
            default -> "application/octet-stream";
        };
    }

    /**
     * 파일명 변환 로그 출력
     */
    public static void logFileNameSanitization(String original, String sanitized) {
        if (!original.equals(sanitized)) {
            log.info("파일명 정규화: {} -> {}", original, sanitized);
        }
    }

    /**
     * 허용된 이미지 확장자 목록 반환
     */
    public static Set<String> getAllowedExtensions() {
        return ALLOWED_EXTENSIONS;
    }
} 
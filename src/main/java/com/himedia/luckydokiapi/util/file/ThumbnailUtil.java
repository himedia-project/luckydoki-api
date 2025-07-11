package com.himedia.luckydokiapi.util.file;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

@Slf4j
public final class ThumbnailUtil {

    private static final int THUMBNAIL_SIZE = 400;
    private static final float WEBP_QUALITY = 0.8f;
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("svg", "jpg", "jpeg", "png", "gif", "webp");

    // 유틸리티 클래스이므로 인스턴스화 방지
    private ThumbnailUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 썸네일 파일 생성 (WebP 변환 지원)
     *
     * @param file 원본 이미지 파일
     * @return 생성된 썸네일 파일 경로
     * @throws IOException 파일 처리 중 오류 발생 시
     */
    public static Path createThumbnail(MultipartFile file) throws IOException {
        validateFile(file);
        
        String extension = getFileExtension(file.getOriginalFilename());
        String baseFileName = getBaseFileName(file.getOriginalFilename());
        
        // WebP 변환 여부 결정 (GIF는 애니메이션 보존을 위해 원본 유지)
        boolean convertToWebP = shouldConvertToWebP(extension);
        String outputExtension = convertToWebP ? "webp" : extension;
        String thumbnailFileName = "s_" + UUID.randomUUID().toString() + "-" + baseFileName + "." + outputExtension;
        
        Path thumbnailPath = Paths.get(thumbnailFileName);
        
        try {
            processThumbnail(file, extension, thumbnailPath);
            validateThumbnailFile(thumbnailPath, thumbnailFileName);
            return thumbnailPath;
        } catch (IOException e) {
            // 실패 시 생성된 파일이 있다면 삭제
            cleanupFile(thumbnailPath);
            throw e;
        }
    }

    /**
     * 썸네일 파일 처리
     */
    private static void processThumbnail(MultipartFile file, String extension, Path thumbnailPath) throws IOException {
        if ("gif".equals(extension)) {
            // GIF 파일은 애니메이션 보존을 위해 원본 그대로 저장
            log.info("GIF 파일은 원본 그대로 저장: {}", file.getOriginalFilename());
            file.transferTo(thumbnailPath.toFile());
        } else if ("webp".equals(extension)) {
            // 이미 WebP인 경우 썸네일만 생성
            log.info("WebP 파일 썸네일 생성: {}", file.getOriginalFilename());
            createWebPThumbnail(file, thumbnailPath);
        } else {
            // 일반 이미지 파일은 WebP로 변환하면서 썸네일 생성
            log.info("이미지 파일 WebP 변환 및 썸네일 생성: {} -> {}", 
                     file.getOriginalFilename(), thumbnailPath.getFileName());
            createWebPThumbnail(file, thumbnailPath);
        }
    }

    /**
     * WebP 썸네일 생성
     */
    private static void createWebPThumbnail(MultipartFile file, Path thumbnailPath) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            Thumbnails.of(inputStream)
                    .size(THUMBNAIL_SIZE, THUMBNAIL_SIZE)
                    .outputFormat("webp")
                    .outputQuality(WEBP_QUALITY)
                    .toFile(thumbnailPath.toFile());
        }
    }

    /**
     * 파일 유효성 검증
     */
    private static void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }
        
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new IllegalArgumentException("파일명이 유효하지 않습니다.");
        }
        
        String extension = getFileExtension(originalFilename);
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("지원되지 않는 파일 형식입니다. 허용된 확장자: " + ALLOWED_EXTENSIONS);
        }
    }

    /**
     * 생성된 썸네일 파일 검증
     */
    private static void validateThumbnailFile(Path thumbnailPath, String thumbnailFileName) throws IOException {
        if (!Files.exists(thumbnailPath) || Files.size(thumbnailPath) == 0) {
            throw new RuntimeException("썸네일 파일 생성에 실패했습니다: " + thumbnailFileName);
        }
        log.info("썸네일 파일 생성 완료: {}, size: {} bytes", thumbnailPath, Files.size(thumbnailPath));
    }

    /**
     * 파일 확장자 추출
     */
    private static String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            throw new IllegalArgumentException("파일 확장자를 찾을 수 없습니다.");
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * 파일명에서 확장자를 제외한 기본 이름 추출
     */
    private static String getBaseFileName(String filename) {
        if (filename == null || !filename.contains(".")) {
            return filename;
        }
        return filename.substring(0, filename.lastIndexOf("."));
    }

    /**
     * WebP 변환 여부 결정
     */
    private static boolean shouldConvertToWebP(String extension) {
        return !"webp".equals(extension) && !"gif".equals(extension);
    }

    /**
     * 파일 정리 (실패 시 임시 파일 삭제)
     */
    public static void cleanupFile(Path filePath) {
        if (filePath != null && Files.exists(filePath)) {
            try {
                Files.delete(filePath);
                log.info("임시 파일 삭제 완료: {}", filePath);
            } catch (IOException e) {
                log.error("임시 파일 삭제 실패: {}", e.getMessage());
            }
        }
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
     * 파일 경로에서 확장자 추출
     */
    public static String getExtensionFromPath(Path path) {
        String filename = path.getFileName().toString();
        return getFileExtension(filename);
    }
} 
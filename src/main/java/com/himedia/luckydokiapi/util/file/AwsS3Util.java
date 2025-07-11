package com.himedia.luckydokiapi.util.file;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;


@Slf4j
@Component
@RequiredArgsConstructor
public class AwsS3Util {

    @Value("${app.props.aws.s3.bucket-name}")
    private String bucketName;

    @Value("${app.props.aws.s3.region}")
    private String region;

    @Value("${app.props.aws.cloudfront.domain}")
    private String cloudfrontDomain;

    @Value("${app.props.aws.cloudfront.enabled:true}")
    private boolean cloudfrontEnabled;

    private final S3Client s3Client;

    /**
     * S3에 파일 업로드
     * @param files 파일 리스트
     * @return 업로드된 파일 URL 리스트
     */
    public List<String> uploadFiles(List<MultipartFile> files) {
        return files.stream()
                .map(this::uploadFile)
                .toList();
    }

    /**
     * S3에 파일 업로드
     *
     * @param file 파일
     * @return 업로드된 파일 URL
     */
    public String uploadFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String extension = Objects.requireNonNull(file.getOriginalFilename())
                .substring(file.getOriginalFilename().lastIndexOf(".") + 1)
                .toLowerCase();

        // 이미지 확장자 검증 (허용된 이미지 확장자 검증)
        Set<String> allowedExtensions = Set.of("svg", "jpg", "jpeg", "png", "gif", "webp");
        if (!allowedExtensions.contains(extension)) {
            throw new IllegalArgumentException("이미지 확장자는 jpg, jpeg, png, svg, gif, webp만 허용됩니다.");
        }

        String originalFilename = file.getOriginalFilename();
        String fileName = UUID.randomUUID().toString() + "-" + originalFilename;

        try {
            // MultipartFile의 입력 스트림을 직접 S3에 업로드
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * S3에 썸네일파일 리스트 업로드
     * @param files 파일 리스트
     * @return 업로드된 400X400 파일 URL 리스트
     */
    public List<String> uploadToThumbnailS3Files(List<MultipartFile> files) {
        return files.stream()
                .map(this::uploadToThumbnailS3File)
                .toList();
    }

    /**
     * S3에 썸네일 파일로 업로드 (WebP 변환 지원)
     * @param file 이미지 파일
     * @return 업로드된 400X400 썸네일 파일 URL (WebP 변환됨)
     */
    public String uploadToThumbnailS3File(MultipartFile file) {
        log.info("uploadToThumbnailS3File: {}", file.getOriginalFilename());
        
        Path thumbnailPath = null;
        
        try {
            // ThumbnailUtil을 사용하여 썸네일 생성
            thumbnailPath = ThumbnailUtil.createThumbnail(file);
            
            // 생성된 썸네일 파일 정보
            String thumbnailFileName = thumbnailPath.getFileName().toString();
            String outputExtension = ThumbnailUtil.getExtensionFromPath(thumbnailPath);
            
            // S3에 업로드 - try-with-resource로 FileInputStream 자동 close
            try (FileInputStream fileInputStream = new FileInputStream(thumbnailPath.toFile())) {
                PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(thumbnailFileName)
                        .contentLength(thumbnailPath.toFile().length())
                        .contentType(ThumbnailUtil.getContentType(outputExtension))
                        .build();
                s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(fileInputStream, thumbnailPath.toFile().length()));
                log.info("S3에 업로드 성공! thumbnailPath: {}, size: {} bytes", thumbnailPath, thumbnailPath.toFile().length());
            }
            
            return thumbnailFileName;
            
        } catch (IOException e) {
            log.error("파일 업로드 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("파일 업로드 중 오류가 발생했습니다: " + e.getMessage());
        } finally {
            // 썸네일 로컬 파일 삭제
            if (thumbnailPath != null) {
                ThumbnailUtil.cleanupFile(thumbnailPath);
            }
        }
    }



    /**
     * CloudFront URL로 파일 경로 가져오기
     * @param fileName 파일 이름
     * @return CloudFront URL
     */
    public String getCloudfrontUrl(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        return "https://" + cloudfrontDomain + "/" + fileName;
    }

    /**
     * S3에 있는 파일 URL 가져오기 (기존 메서드 대체)
     * @param fileName 파일 이름
     * @return CloudFront 또는 S3 URL
     */
    public String getUrl(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        
        // CloudFront 활성화되어 있으면 CloudFront URL 반환
        if (cloudfrontEnabled) {
            return getCloudfrontUrl(fileName);
        }
        
        // 그렇지 않으면 S3 URL 반환 (v2 방식)
        GetUrlRequest getUrlRequest = GetUrlRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();
        return s3Client.utilities().getUrl(getUrlRequest).toString();
    }

    /**
     * S3에 있는 파일 가져오기 (기존 메서드 수정)
     * @param fileName 파일 이름
     * @return 파일 리소스
     * @throws IOException 파일이 없을 경우 예외 발생
     */
    public ResponseEntity<Resource> getFile(String fileName) throws IOException {
        // CloudFront URL로 변경
        String urlStr = getUrl(fileName);
        
        HttpHeaders headers = new HttpHeaders();
        try {
            URL url = new URL(urlStr);
            URLConnection urlConnection = url.openConnection();
            // 캐싱 최적화를 위한 헤더 추가
            urlConnection.setRequestProperty("Cache-Control", "max-age=31536000");
            
            // try-with-resources를 사용하여 InputStream 자동으로 닫기
            try (InputStream inputStream = urlConnection.getInputStream()) {
                byte[] data = inputStream.readAllBytes();
                Resource resource = new ByteArrayResource(data);
                
                // MIME 타입 설정
                String mimeType = urlConnection.getContentType();
                if (mimeType == null) {
                    Path path = Paths.get(fileName);
                    mimeType = Files.probeContentType(path);
                }
                headers.add("Content-Type", mimeType);
                // 캐시 관련 헤더 추가
                headers.add("Cache-Control", "max-age=31536000, public");
                
                return ResponseEntity.ok().headers(headers).body(resource);
            }
        } catch (IOException e) {
            log.error("CloudFront 파일 가져오기 오류: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * S3에 파일 삭제
     * @param fileNames 파일 이름 리스트
     */
    public void deleteFiles(List<String> fileNames) {
        for (String fileName : fileNames) {
            this.deleteFile(fileName);
        }
    }

    /**
     * S3에 파일 삭제
     * @param fileName  파일 이름
     */
    public void deleteFile(String fileName) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();
        s3Client.deleteObject(deleteObjectRequest);
    }

    /**
     * S3에 있는 파일 리소스 가져오기
     * @param fileName 파일 이름
     * @return 파일 리소스
     * @throws IOException 파일이 없을 경우 예외 발생
     */
    public Resource getResource(String fileName) throws IOException {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();
        
        try (InputStream inputStream = s3Client.getObject(getObjectRequest)) {
            byte[] data = inputStream.readAllBytes();
            return new ByteArrayResource(data);
        }
    }
}

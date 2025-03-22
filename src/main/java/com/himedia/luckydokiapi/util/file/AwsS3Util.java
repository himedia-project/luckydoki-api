package com.himedia.luckydokiapi.util.file;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

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

    private final AmazonS3 s3Client;

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

        checkImageExtension(extension);

        String originalFilename = file.getOriginalFilename();
        String fileName = UUID.randomUUID().toString() + "-" + originalFilename;

        try {
            // MultipartFile의 입력 스트림을 직접 S3에 업로드
            s3Client.putObject(bucketName, fileName, file.getInputStream(), null);
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
     * S3에 썸네일 파일로 업로드
     * @param file 이미지 파일
     * @return 업로드된 400X400 썸네일 파일 URL
     */
    public String uploadToThumbnailS3File(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String extension = Objects.requireNonNull(file.getOriginalFilename())
                .substring(file.getOriginalFilename().lastIndexOf(".") + 1)
                .toLowerCase();

        checkImageExtension(extension);

        String originalFilename = file.getOriginalFilename();
        String thumbnailFileName = "s_" + UUID.randomUUID().toString() + "-" + originalFilename;
        Path thumbnailPath = null;
        try {
            thumbnailPath = Paths.get(thumbnailFileName);
            
            // WebP, GIF 파일인 경우와 그 외 이미지 파일 처리를 분리
            if ("webp".equals(extension) || "gif".equals(extension)) {
                // WebP, GIF 파일은 원본 그대로 저장
                file.transferTo(thumbnailPath.toFile());
            } else {
                // 일반 이미지 파일은 썸네일 생성
                Thumbnails.of(file.getInputStream())
                        .size(400, 400)
                        .outputFormat(extension)
                        .toFile(thumbnailPath.toFile());
            }

            // S3에 업로드
            s3Client.putObject(new PutObjectRequest(bucketName, thumbnailPath.toFile().getName(), thumbnailPath.toFile()));
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 중 오류가 발생했습니다: " + e.getMessage());
        } finally {
            // 썸네일 로컬 파일 삭제
            if (thumbnailPath != null && Files.exists(thumbnailPath)) {
                log.info("local thumbnailPath exist! {}", thumbnailPath);
                try {
                    Files.delete(thumbnailPath);
                } catch (IOException e) {
                    log.error("Failed to delete local thumbnail file: {}", e.getMessage());
                }
            }
        }
        return thumbnailFileName;
    }

    /**
     * 이미지 확장자 검증
     *
     * @param extension 이미지 확장자
     */
    private void checkImageExtension(String extension) {
        // 허용된 이미지 확장자 검증 (WebP 포함)
        Set<String> allowedExtensions = Set.of("svg", "jpg", "jpeg", "png", "gif", "webp");
        if (!allowedExtensions.contains(extension)) {
            throw new IllegalArgumentException("이미지 확장자는 jpg, jpeg, png, svg, gif, webp만 허용됩니다.");
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
        
        // 그렇지 않으면 S3 URL 반환
        return s3Client.getUrl(bucketName, fileName).toString();
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

//        String urlStr = s3Client.getUrl(bucketName, fileName).toString();
        
        Resource resource;
        HttpHeaders headers = new HttpHeaders();
        try {
            URL url = new URL(urlStr);
            URLConnection urlConnection = url.openConnection();
            // 캐싱 최적화를 위한 헤더 추가
            urlConnection.setRequestProperty("Cache-Control", "max-age=31536000");
            InputStream inputStream = urlConnection.getInputStream();
            resource = new InputStreamResource(inputStream);

            // MIME 타입 설정
            String mimeType = urlConnection.getContentType();
            if (mimeType == null) {
                Path path = Paths.get(fileName);
                mimeType = Files.probeContentType(path);
            }
            headers.add("Content-Type", mimeType);
            // 캐시 관련 헤더 추가
            headers.add("Cache-Control", "max-age=31536000, public");
        } catch (IOException e) {
            log.error("CloudFront 파일 가져오기 오류: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok().headers(headers).body(resource);
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
        s3Client.deleteObject(bucketName, fileName);
    }

    /**
     * S3에 있는 파일 URL 가져오기
     * @param fileName 파일 이름
     * @return 파일 URL
     */
    /*
    public String getUrl(String fileName) {
        return s3Client.getUrl(bucketName, fileName).toString();
    }*/


    /**
     * CloudFront에서 파일 리소스 가져오기 (기존 메서드 수정)
     * @param fileName 파일 이름
     * @return 파일 리소스
     * @throws IOException 파일을 가져오는 중 오류 발생 시
     */
    public Resource getResource(String fileName) throws IOException {
        // CloudFront URL로 변경
        String urlStr = getUrl(fileName);
        /*
        // S3 URL 가져오기
        String urlStr = s3Client.getUrl(bucketName, fileName).toString();
        */
        
        try {
            URL url = new URL(urlStr);
            URLConnection urlConnection = url.openConnection();
            // 캐싱 최적화
            // max-age=31536000: 1년 동안 캐싱
            urlConnection.setRequestProperty("Cache-Control", "max-age=31536000");
            InputStream inputStream = urlConnection.getInputStream();
            
            // InputStreamResource 생성 및 반환
            return new InputStreamResource(inputStream);
        } catch (IOException e) {
            log.error("CloudFront 리소스 가져오기 오류: {}", e.getMessage());
            throw new IOException("CloudFront에서 리소스를 가져오는 중 오류 발생: " + fileName, e);
        }
    }

}

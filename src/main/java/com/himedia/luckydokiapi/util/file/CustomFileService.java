package com.himedia.luckydokiapi.util.file;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomFileService {

    private final AwsS3Util s3Util;

//    @PostConstruct
//    public void init() {
//        File tempFolder = new File(uploadPath);
//
//        if (!tempFolder.exists()) {
//            tempFolder.mkdir();
//        }
//
//        uploadPath = tempFolder.getAbsolutePath();
//
//        log.info("-------------------------------------");
//        log.info(uploadPath);
//    }


    // === S3 관련 메서드들 ===

    /**
     * 파일 s3 업로드
     * @param file 업로드할 파일
     * @return 업로드된 파일명
     */
    public String uploadS3File(MultipartFile file) {
        return s3Util.uploadFile(file);
    }

    /**
     * 파일리스트 s3 업로드
     *
     * @param files 업로드할 파일 리스트
     * @return 업로드된 파일명 리스트
     * @throws RuntimeException
     */
    public List<String> uploadS3Files(List<MultipartFile> files) {
        return s3Util.uploadFiles(files);
    }

    /**
     * 파일 썸네일로 s3 업로드
     * @param file 업로드할 파일
     * @return 업로드된 파일명
     */
    public String uploadToThumbnailS3File(MultipartFile file) {
        return s3Util.uploadToThumbnailS3File(file);
    }

    /**
     * 파일리스트 썸네일로 s3 업로드
     * @param files 업로드할 파일 리스트
     * @return 업로드된 파일명 리스트
     */
    public List<String> uploadToThumbnailS3Files(List<MultipartFile> files) {
        return s3Util.uploadToThumbnailS3Files(files);
    }

    /**
     * 파일 가져오기
     * @param fileName 파일명
     * @return 파일 리소스
     */
    public ResponseEntity<Resource> getFile(String fileName) {
        try {
            return s3Util.getFile(fileName);
        } catch (IOException e) {
            log.error("getFile error: {}", e.getMessage());
            throw new RuntimeException("Failed to get file: " + fileName);
        }
    }

    /**
     * s3 파일 삭제
     * @param fileNames 삭제할 파일명 리스트
     */
    public void deleteS3Files(List<String> fileNames) {
        if (fileNames == null || fileNames.isEmpty()) {
            return;
        }
        s3Util.deleteFiles(fileNames);
    }

    /**
     * s3 파일 삭제
     * @param fileName 삭제할 파일명
     */
    public void deleteS3File(String fileName) {
        if (fileName == null) {
            return;
        }
        s3Util.deleteFile(fileName);
    }

    /**
     * 이미지 URL을 MultipartFile로 변환
     * @param imageUrl 이미지 URL
     * @return MultipartFile
     * @throws IOException
     */
    public MultipartFile convert(String imageUrl) throws IOException {

        if (imageUrl == null) {
            return null;
        }

        // url http: 로 시작하면 -> https: 로 변경
        if (imageUrl.startsWith("http:")) {
            imageUrl = imageUrl.replace("http:", "https:");
        }

        log.info("convert imageUrl: {}", imageUrl);

        // Check if the file exists at the given URL
        URLConnection connection = null;
        URL url = null;
        InputStream inputStream = null;

        try {
            url = new URL(imageUrl);
            connection = url.openConnection();
            connection.connect();

            inputStream = connection.getInputStream();
            byte[] bytes = StreamUtils.copyToByteArray(inputStream);

            // Create a MultipartFile object from the byte array
            String finalImageUrl = imageUrl;
            MultipartFile multipartFile = new MultipartFile() {
                @Override
                public String getName() {
                    return "file";
                }

                @Override
                public String getOriginalFilename() {
                    String[] segments = finalImageUrl.split("/");
                    return segments[segments.length - 1].replaceAll("\\?.*", "");
                }

                @Override
                public String getContentType() {
                    return URLConnection.guessContentTypeFromName(getOriginalFilename());
                }

                @Override
                public boolean isEmpty() {
                    return bytes.length == 0;
                }

                @Override
                public long getSize() {
                    return bytes.length;
                }

                @Override
                public byte[] getBytes() throws IOException {
                    return bytes;
                }

                @Override
                public InputStream getInputStream() throws IOException {
                    return new ByteArrayInputStream(bytes);
                }

                @Override
                public void transferTo(File dest) throws IOException, IllegalStateException {
                    new FileOutputStream(dest).write(bytes);
                }
            };

            return multipartFile;

        } catch (FileNotFoundException e) {
            log.error("File not found at the given URL: {}", imageUrl);
            return null;
        } catch (IOException e) {
            throw new RuntimeException("Failed to download file from URL: " + imageUrl);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (connection != null) {
                connection.getInputStream().close();
            }
        }
    }


    /**
     * 이미지 URL을 MultipartFile로 변환 후 s3에 저장
     * @param imagePathList 이미지 URL 리스트
     * @return 저장된 파일명 리스트
     */
    public List<String> uploadImagePathS3Files(List<String> imagePathList) {
        List<MultipartFile> multipartFiles = new ArrayList<>();

        for (String imagePath : imagePathList) {
            try {
                MultipartFile multipartFile = convert(imagePath);
                if (multipartFile != null) {
                    multipartFiles.add(multipartFile);
                }
            } catch (IOException e) {
                log.error("uploadImagePathS3Files error: {}", e.getMessage());
                throw new RuntimeException("Failed to download file from URL: " + imagePath);
            }
        }
        return this.uploadS3Files(multipartFiles);
    }

    /**
     * CloudFront/S3에서 이미지 URL 가져오기
     * @param fileName 이미지 파일 이름
     * @return CloudFront URL
     */
    public String getS3Url(String fileName) {
        return s3Util.getUrl(fileName);
    }

    /**
     * S3에 저장된 이미지 경로를 CloudFront URL로 변환하여 가져온다.
     * @param stringList 이미지 파일 이름 리스트
     * @return merged한 CloudFront 이미지 경로 리스트 문자열
     */
    public String getMergedS3ImagePathList(List<String> stringList) {
        if (stringList == null || stringList.isEmpty()) {
            return "";
        }

        List<String> resultList = new ArrayList<>();

        for (String fileName : stringList) {
            if (fileName != null && !fileName.isEmpty()) {
                resultList.add(this.getS3Url(fileName));
            }
        }

        return String.join(",", resultList);
    }


    /**
     * CloudFront에서 파일 리소스 가져오기
     * @param fileName 파일명
     * @return 파일 리소스
     */
    public Resource getFileResource(String fileName) {
        try {
            return s3Util.getResource(fileName);
        } catch (IOException e) {
            log.error("getFileResource 오류: {}", e.getMessage());
            throw new RuntimeException("CloudFront에서 파일 리소스를 가져오는데 실패했습니다: " + fileName);
        }
    }

    // === 파일명 관련 유틸리티 메서드들 ===
    // 파일명 관련 유틸리티 메서드들은 FileNameUtil 클래스로 이동되었습니다.
    // 사용법: FileNameUtil.getFileExtension(filename), FileNameUtil.sanitizeFileName(filename) 등
}

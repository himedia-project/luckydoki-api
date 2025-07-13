package com.himedia.luckydokiapi.domain.image.controller;


import com.himedia.luckydokiapi.util.file.CustomFileService;
import com.himedia.luckydokiapi.util.file.FileNameUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/image")
@Tag(name = "image-api", description = "이미지 upload , view 관련 api")
public class ImageController {

    private final CustomFileService fileService;

    @Operation(
            summary = "이미지 조회 (레거시)",
            description = "CloudFront를 통해 이미지를 조회합니다. 레거시 API입니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "이미지 조회 성공"),
                    @ApiResponse(responseCode = "404", description = "이미지를 찾을 수 없음"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    @GetMapping("/view/{fileName}")
    public ResponseEntity<Resource> viewFileGET(@Parameter(description = "화면에 보여잘 파일명") @PathVariable String fileName) {
        return fileService.getFile(fileName);
    }


    @Operation(
            summary = "이미지 조회",
            description = "CloudFront를 통해 이미지를 조회합니다. 최적의 캐싱 설정이 적용됩니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "이미지 조회 성공"),
                    @ApiResponse(responseCode = "404", description = "이미지를 찾을 수 없음"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    @GetMapping("/view2/{fileName}")
    public ResponseEntity<Resource> view2FileGET(
            @Parameter(description = "조회할 이미지 파일명")
            @PathVariable String fileName) {
        try {
            // CloudFront에서 리소스 가져오기
            Resource resource = fileService.getFileResource(fileName);

            // 파일 확장자에 따른 미디어 타입 결정
            String mediaType = determineMediaType(fileName);

            return ResponseEntity.ok()
                    // CloudFront의 캐싱 전략과 일치하도록 1년 캐시 설정
                    .cacheControl(CacheControl.maxAge(365, TimeUnit.DAYS).cachePublic())
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                    .contentType(MediaType.parseMediaType(mediaType))
                    .body(resource);
        } catch (Exception e) {
            log.error("이미지 조회 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "이미지 업로드",
            description = "S3에 이미지를 업로드.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "업로드 성공", content = @Content(schema = @Schema(type = "string"), mediaType = "text/plain")),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 파일 형식")
            }
    )
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFilePOST(@RequestPart("file") MultipartFile file) {
        log.info("Image upload request received: {}", file.getOriginalFilename());
        return ResponseEntity.ok(fileService.uploadS3File(file));
    }


    @Operation(
            summary = "썸네일 이미지 업로드",
            description = "S3에 썸네일 이미지를 업로드.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "업로드 성공", content = @Content(schema = @Schema(type = "string"), mediaType = "text/plain")),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 파일 형식")
            }
    )
    @PostMapping("/upload/thumbnail")
    public ResponseEntity<String> thumbnailUploadFilePOST(@RequestPart("file") MultipartFile file) {
        log.info("Thumbnail upload request received: {}", file.getOriginalFilename());
        return ResponseEntity.ok(fileService.uploadToThumbnailS3File(file));
    }

    /**
     * 파일 이름에서 미디어 타입 결정
     *
     * @param fileName 파일 이름
     * @return 미디어 타입
     */
    private String determineMediaType(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        return FileNameUtil.getContentType(extension);
    }
}

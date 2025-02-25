package com.himedia.luckydokiapi.domain.image.controller;


import com.himedia.luckydokiapi.util.file.CustomFileUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/image")
@Tag(name = "image-api", description = "이미지 upload , view 관련 api")
public class ImageController {

    private final CustomFileUtil fileUtil;

    @GetMapping("/view/{fileName}")
    public ResponseEntity<Resource> viewFileGET(@Parameter(description = "화면에 보여잘 파일명") @PathVariable String fileName) {
        return fileUtil.getFile(fileName);
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
        return ResponseEntity.ok(fileUtil.uploadToThumbnailS3File(file));
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
        return ResponseEntity.ok(fileUtil.uploadS3File(file));
    }
}

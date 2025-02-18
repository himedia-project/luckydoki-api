package com.himedia.luckydokiapi.domain.image.controller;


import com.himedia.luckydokiapi.util.file.CustomFileUtil;
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
public class ImageController {

    private final CustomFileUtil fileUtil;

    @GetMapping("/view/{fileName}")
    public ResponseEntity<Resource> viewFileGET(@PathVariable String fileName) {
        return fileUtil.getFile(fileName);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFilePOST(@RequestPart("file") MultipartFile file) {
        return ResponseEntity.ok(fileUtil.uploadS3File(file));
    }
}

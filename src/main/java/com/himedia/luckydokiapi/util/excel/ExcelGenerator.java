package com.himedia.luckydokiapi.util.excel;


import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ExcelGenerator {

    public static <T> ByteArrayResource generateExcelFile(List<T> data, String sheetName) {
        try {
            SimpleExcelFile<T> userInfoFile = new SimpleExcelFile<>(data, sheetName);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            userInfoFile.write(byteArrayOutputStream);
            return new ByteArrayResource(byteArrayOutputStream.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("엑셀 파일 생성 중 오류가 발생했습니다.");
        }
    }

    public static ResponseEntity<ByteArrayResource> createResponseEntity(ByteArrayResource byteArrayResource, String fileName) {
        return ResponseEntity.ok()
                .headers(createHttpHeaders(fileName))
                .contentLength(byteArrayResource.contentLength())
                .body(byteArrayResource);
    }

    private static HttpHeaders createHttpHeaders(String fileName) {
        HttpHeaders headers = new HttpHeaders();
        // headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename(URLEncoder.encode(fileName, StandardCharsets.UTF_8))
                .build());
        return headers;
    }
}

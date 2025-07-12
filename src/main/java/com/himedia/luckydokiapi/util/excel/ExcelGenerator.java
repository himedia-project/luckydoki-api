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

    /**
     * 엑셀 파일을 생성합니다.
     * @param data 엑셀에 포함될 데이터 리스트
     * @param sheetName 엑셀 시트 이름
     * @return ByteArrayResource 엑셀 파일의 ByteArrayResource
     * @param <T> 데이터 타입
     */
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

    /**
     * ByteArrayResource를 사용하여 ResponseEntity를 생성합니다.
     * @param byteArrayResource 엑셀 파일의 ByteArrayResource
     * @param fileName 다운로드할 파일 이름
     * @return ResponseEntity<ByteArrayResource> 객체
     */
    public static ResponseEntity<ByteArrayResource> createResponseEntity(ByteArrayResource byteArrayResource, String fileName) {
        return ResponseEntity.ok()
                .headers(createHttpHeaders(fileName))
                .contentLength(byteArrayResource.contentLength())
                .body(byteArrayResource);
    }

    /**
     * HTTP 헤더를 생성합니다.
     * @param fileName 다운로드할 파일 이름
     * @return 생성된 HttpHeaders 객체
     */
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

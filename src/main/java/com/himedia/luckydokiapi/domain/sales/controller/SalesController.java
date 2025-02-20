package com.himedia.luckydokiapi.domain.sales.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.himedia.luckydokiapi.domain.sales.dto.PythonRequest;
import com.himedia.luckydokiapi.domain.sales.dto.SalesData;
import com.himedia.luckydokiapi.domain.sales.service.SalesService;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sales")
@Log4j2
@RequiredArgsConstructor
public class SalesController {

  private final SalesService salesService;

  @PostMapping("/forecast")
  public ResponseEntity<?> getSalesForecast() {
    try {
      // 1) DB 등에서 데이터 조회
      List<SalesData> salesDataList = salesService.getDailySalesData();
      log.info("Sales Data: {}", salesDataList);

      // 2) JSON 직렬화
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.registerModule(new JavaTimeModule());
      objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

      String jsonData = objectMapper.writeValueAsString(salesDataList);
      log.info("Serialized JSON: {}", jsonData);

      // 3) ClassPathResource로 파이썬 스크립트 로드 → 임시 파일로 복사
      ClassPathResource resource = new ClassPathResource("python/sales_forecast.py");
      File tempScriptFile = File.createTempFile("sales_forecast", ".py");
      tempScriptFile.deleteOnExit(); // JVM 종료 시 임시 파일 삭제

      // 3) Python 스크립트 실행
      String pythonFilePath = "F:/notebook/luckydoki/luckydoki-api/src/main/resources/python/sales_forecast.py";
      ProcessBuilder processBuilder = new ProcessBuilder("python3", pythonFilePath);
      processBuilder.redirectErrorStream(false);
      Process process = processBuilder.start();

      // 5) Python에 JSON 데이터 전달
      try (BufferedWriter writer = new BufferedWriter(
          new OutputStreamWriter(process.getOutputStream(), "UTF-8"))) {
        writer.write(jsonData);
        writer.flush();
      }

      // 6) Python stdout 읽기
      BufferedReader stdOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
      StringBuilder outBuilder = new StringBuilder();
      String line;
      while ((line = stdOut.readLine()) != null) {
        outBuilder.append(line);
      }
      stdOut.close();

      // 7) Python stderr 읽기
      BufferedReader stdErr = new BufferedReader(new InputStreamReader(process.getErrorStream()));
      StringBuilder errBuilder = new StringBuilder();
      while ((line = stdErr.readLine()) != null) {
        errBuilder.append(line);
      }
      stdErr.close();

      log.info("Python STDOUT: {}", outBuilder.toString());
      log.error("Python STDERR: {}", errBuilder.toString());

      String output = outBuilder.toString();
      if (output == null || output.trim().isEmpty()) {
        throw new RuntimeException("Python script returned no output");
      }

      // 8) Python 출력(JSON)을 다시 파싱
      Map<String, Object> result = objectMapper.readValue(output, Map.class);

      return ResponseEntity.ok(result);

    } catch (Exception e) {
      log.error("Error in getSalesForecast(): ", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Collections.singletonMap("error", "예측 실패: " + e.getMessage()));
    }
  }

  /**
   * /api/sales/forecast/date
   * 요청 바디(selectedDate)를 받아 해당 날짜의 매출 데이터를 조회 후 Python에 전달, 예측값을 받아옴
   */
  @PostMapping("/forecast/date")
  public ResponseEntity<?> getSalesForecastByDate(@RequestBody Map<String, Object> requestBody) {
    try {
      String selectedDate = (String) requestBody.get("selectedDate");
      List<SalesData> salesDataList = salesService.getSalesDataByDate(selectedDate);
      log.info("Sales Data for {}: {}", selectedDate, salesDataList);

      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.registerModule(new JavaTimeModule());
      objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

      String selectedDateValue = (selectedDate != null ? selectedDate : "");
      PythonRequest pythonRequest = new PythonRequest(salesDataList, selectedDateValue);
      String jsonInput = objectMapper.writeValueAsString(pythonRequest);
      log.info("Serialized JSON to Python: {}", jsonInput);

      // 1) 마찬가지로 ClassPathResource로 파이썬 스크립트 로드
      ClassPathResource resource = new ClassPathResource("python/sales_forecast.py");
      File tempScriptFile = File.createTempFile("sales_forecast", ".py");
      tempScriptFile.deleteOnExit();

      try (InputStream is = resource.getInputStream();
          FileOutputStream fos = new FileOutputStream(tempScriptFile)) {
        StreamUtils.copy(is, fos);
      }

      // 2) 파이썬 프로세스 실행
      ProcessBuilder processBuilder = new ProcessBuilder("python", tempScriptFile.getAbsolutePath());
      processBuilder.redirectErrorStream(false);
      Process process = processBuilder.start();

      // 3) JSON 데이터 전달
      try (BufferedWriter writer = new BufferedWriter(
          new OutputStreamWriter(process.getOutputStream(), "UTF-8"))) {
        writer.write(jsonInput);
        writer.flush();
      }

      // 4) stdout 읽기
      BufferedReader stdOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
      StringBuilder outBuilder = new StringBuilder();
      String line;
      while ((line = stdOut.readLine()) != null) {
        outBuilder.append(line);
      }
      stdOut.close();

      // 5) stderr 읽기
      BufferedReader stdErr = new BufferedReader(new InputStreamReader(process.getErrorStream()));
      StringBuilder errBuilder = new StringBuilder();
      while ((line = stdErr.readLine()) != null) {
        errBuilder.append(line);
      }
      stdErr.close();

      log.info("Python STDOUT: {}", outBuilder.toString());
      log.error("Python STDERR: {}", errBuilder.toString());

      String output = outBuilder.toString();
      if (output == null || output.trim().isEmpty()) {
        throw new RuntimeException("Python script returned no output");
      }

      Map<String, Object> result = objectMapper.readValue(output, Map.class);
      return ResponseEntity.ok(result);

    } catch (Exception e) {
      log.error("Error in getSalesForecastByDate(): ", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Collections.singletonMap("error", "예측 실패: " + e.getMessage()));
    }
  }
}

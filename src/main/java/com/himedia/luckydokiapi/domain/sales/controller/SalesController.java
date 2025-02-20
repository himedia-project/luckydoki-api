package com.himedia.luckydokiapi.domain.sales.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.himedia.luckydokiapi.domain.sales.dto.PythonRequest;
import com.himedia.luckydokiapi.domain.sales.dto.SalesData;
import com.himedia.luckydokiapi.domain.sales.service.SalesService;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

  private String getPythonScriptPath() {
    String os = System.getProperty("os.name").toLowerCase();
    String resourcePath = "src/main/resources/python/sales_forecast.py";
    
    if (os.contains("win")) {
      return "src\\main\\resources\\python\\sales_forecast.py";
    } else {
      return resourcePath;
    }
  }

  @PostMapping("/forecast")
  public ResponseEntity<?> getSalesForecast() {
    try {
      // 1) DB 등에서 데이터 조회 (예: SalesService)
      // SalesData는 'date'(String or LocalDateTime) + 'totalSales'(Double) 필드를 가정
      List<SalesData> salesDataList = salesService.getDailySalesData();
      log.info("Sales Data: " + salesDataList);

      // 2) JSON 직렬화
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.registerModule(new JavaTimeModule());
      objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

      String jsonData = objectMapper.writeValueAsString(salesDataList);
      log.info("Serialized JSON: " + jsonData);

      // 3) Python 스크립트 실행
      String pythonFilePath = getPythonScriptPath();
      ProcessBuilder processBuilder = new ProcessBuilder("python", pythonFilePath);
      processBuilder.redirectErrorStream(false);
      Process process = processBuilder.start();

      // 4) Python에 JSON 데이터 전달
      try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream(), "UTF-8"))) {
        writer.write(jsonData);
        writer.flush();
      }

      // 5) Python stdout 결과 읽기
      BufferedReader stdOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
      StringBuilder outBuilder = new StringBuilder();
      String line;
      while ((line = stdOut.readLine()) != null) {
        outBuilder.append(line);
      }
      stdOut.close();

      // 5-1) Python stderr 결과 읽기
      BufferedReader stdErr = new BufferedReader(new InputStreamReader(process.getErrorStream()));
      StringBuilder errBuilder = new StringBuilder();
      while ((line = stdErr.readLine()) != null) {
        errBuilder.append(line);
      }
      stdErr.close();

      log.info("Python STDOUT: " + outBuilder.toString());
      log.error("Python STDERR: " + errBuilder.toString());

      // 6) 결과 파싱
      String output = outBuilder.toString();
      if (output == null || output.trim().isEmpty()) {
        throw new RuntimeException("Python script returned no output");
      }

      // Python 스크립트가 JSON으로 결과를 반환하므로 Map으로 파싱
      Map<String, Object> result = objectMapper.readValue(output, Map.class);

      // 예: {"forecast_message":"...","image_url":"/static/sales_trend.png"}
      return ResponseEntity.ok(result);

    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Collections.singletonMap("error", "예측 실패: " + e.getMessage()));
    }
  }

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
      log.info("Serialized JSON to Python: " + jsonInput);

      String pythonFilePath = getPythonScriptPath();
      ProcessBuilder processBuilder = new ProcessBuilder("python", pythonFilePath);
      processBuilder.redirectErrorStream(false);
      Process process = processBuilder.start();

      // Python 프로세스에 JSON 데이터 전달
      try (BufferedWriter writer = new BufferedWriter(
          new OutputStreamWriter(process.getOutputStream(), "UTF-8"))) {
        writer.write(jsonInput);
        writer.flush();
      }

      // Python stdout 읽기
      BufferedReader stdOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
      StringBuilder outBuilder = new StringBuilder();
      String line;
      while ((line = stdOut.readLine()) != null) {
        outBuilder.append(line);
      }
      stdOut.close();

      // Python stderr 읽기
      BufferedReader stdErr = new BufferedReader(new InputStreamReader(process.getErrorStream()));
      StringBuilder errBuilder = new StringBuilder();
      while ((line = stdErr.readLine()) != null) {
        errBuilder.append(line);
      }
      stdErr.close();

      log.info("Python STDOUT: " + outBuilder.toString());
      log.error("Python STDERR: " + errBuilder.toString());

      String output = outBuilder.toString();
      if (output == null || output.trim().isEmpty()) {
        throw new RuntimeException("Python script returned no output");
      }

      Map<String, Object> result = objectMapper.readValue(output, Map.class);
      return ResponseEntity.ok(result);
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Collections.singletonMap("error", "예측 실패: " + e.getMessage()));
    }
  }
}

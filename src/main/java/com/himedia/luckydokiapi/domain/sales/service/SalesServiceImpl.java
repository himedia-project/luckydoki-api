package com.himedia.luckydokiapi.domain.sales.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.himedia.luckydokiapi.domain.sales.dto.SalesData;
import com.himedia.luckydokiapi.domain.sales.repository.SalesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class SalesServiceImpl implements SalesService {

  private final SalesRepository salesRepository;

  /**
   * DB에서 모든 일별 매출 데이터를 조회
   */
  @Transactional(readOnly = true)
  @Override
  public List<SalesData> getDailySalesData() {
    List<Object[]> rows = salesRepository.findDailySalesNative();
    List<SalesData> salesDataList = new ArrayList<>();

    for (Object[] row : rows) {
      java.util.Date date = (java.util.Date) row[0];
      Double totalSales = ((Number) row[1]).doubleValue();
      salesDataList.add(new SalesData(date, totalSales));
    }
    return salesDataList;
  }

  /**
   * DB에서 특정 날짜(selectedDate)의 매출 데이터 조회
   */
  @Transactional(readOnly = true)
  @Override
  public List<SalesData> getSalesDataByDate(String selectedDate) {
    List<Object[]> rows = salesRepository.findDailySalesNative();
    List<SalesData> salesDataList = new ArrayList<>();

    for (Object[] row : rows) {
      java.util.Date date = (java.util.Date) row[0];
      String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(date);

      if (selectedDate != null && selectedDate.equals(dateStr)) {
        Double totalSales = ((Number) row[1]).doubleValue();
        salesDataList.add(new SalesData(date, totalSales));
      }
    }
    return salesDataList;
  }

  /**
   * (A) 전체 데이터 기반으로 파이썬 스크립트 실행
   */
  @Override
  public Map<String, Object> getSalesForecast() {
    try {
      // 1) DB조회
      List<SalesData> salesDataList = getDailySalesData();
      log.info("Sales Data: {}", salesDataList);

      // 2) JSON 직렬화
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.registerModule(new JavaTimeModule());
      objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

      String jsonData = objectMapper.writeValueAsString(salesDataList);
      log.info("Serialized JSON: {}", jsonData);

      // 3) 파이썬 스크립트 로드 → 임시 파일에 복사
      ClassPathResource resource = new ClassPathResource("python/sales_forecast.py");
      File tempScriptFile = File.createTempFile("sales_forecast", ".py");
      tempScriptFile.deleteOnExit();
      try (InputStream is = resource.getInputStream();
          FileOutputStream fos = new FileOutputStream(tempScriptFile)) {
        StreamUtils.copy(is, fos);
      }

      log.info("Temp Python script file: {}", tempScriptFile.getAbsolutePath());

      // 4) ProcessBuilder (stderr와 stdout 분리: redirectErrorStream(false))
      ProcessBuilder processBuilder = new ProcessBuilder(
          "python3",
          tempScriptFile.getAbsolutePath()
      );
      processBuilder.redirectErrorStream(false); // <-- 핵심: 분리
      Process process = processBuilder.start();

      // 5) 자바 → 파이썬 stdin
      try (BufferedWriter writer = new BufferedWriter(
          new OutputStreamWriter(process.getOutputStream(), StandardCharsets.UTF_8))) {
        writer.write(jsonData);
        writer.flush();
      }

      // 6) 자바 ← 파이썬 stdout
      StringBuilder outBuilder = new StringBuilder();
      try (BufferedReader stdOut = new BufferedReader(
          new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
        String line;
        while ((line = stdOut.readLine()) != null) {
          outBuilder.append(line);
        }
      }

      // 7) 자바 ← 파이썬 stderr
      StringBuilder errBuilder = new StringBuilder();
      try (BufferedReader stdErr = new BufferedReader(
          new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8))) {
        String line;
        while ((line = stdErr.readLine()) != null) {
          errBuilder.append(line).append("\n");
        }
      }

      // 8) 프로세스 종료 대기
      int exitCode = process.waitFor();
      log.info("Python process finished with exit code: {}", exitCode);

      log.info("Python STDOUT: {}", outBuilder);
      log.info("Python STDERR: {}", errBuilder);

      if (exitCode != 0) {
        throw new RuntimeException("Python script exited with code " + exitCode
            + "\n" + errBuilder.toString());
      }

      // 9) 파이썬 출력(JSON) 파싱
      String output = outBuilder.toString().trim();
      if (output.isEmpty()) {
        throw new RuntimeException("Python script returned no output");
      }

      Map<String, Object> result = objectMapper.readValue(output, Map.class);
      return result;

    } catch (Exception e) {
      log.error("Error in getSalesForecast(): ", e);
      throw new RuntimeException("예측 실패: " + e.getMessage(), e);
    }
  }

  /**
   * (B) 특정 날짜 데이터 기반으로 파이썬 스크립트 실행
   */
  @Override
  public Map<String, Object> getSalesForecastByDate(String selectedDate) {
    try {
      // 1) DB조회
      List<SalesData> salesDataList = getSalesDataByDate(selectedDate);
      log.info("Sales Data for {}: {}", selectedDate, salesDataList);

      // 2) JSON 직렬화
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.registerModule(new JavaTimeModule());
      objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

      // Python에 넘길 JSON: {"salesData": [...], "selectedDate": "..."}
      Map<String, Object> pythonRequest = new HashMap<>();
      pythonRequest.put("salesData", salesDataList);
      pythonRequest.put("selectedDate", selectedDate);

      String jsonData = objectMapper.writeValueAsString(pythonRequest);
      log.info("Serialized JSON to Python: {}", jsonData);

      // 3) 파이썬 스크립트 임시 파일
      ClassPathResource resource = new ClassPathResource("python/sales_forecast.py");
      File tempScriptFile = File.createTempFile("sales_forecast", ".py");
      tempScriptFile.deleteOnExit();
      try (InputStream is = resource.getInputStream();
          FileOutputStream fos = new FileOutputStream(tempScriptFile)) {
        StreamUtils.copy(is, fos);
      }

      log.info("Temp Python script file: {}", tempScriptFile.getAbsolutePath());

      // 4) ProcessBuilder (stderr와 stdout 분리)
      ProcessBuilder processBuilder = new ProcessBuilder(
          "python3",
          tempScriptFile.getAbsolutePath()
      );
      processBuilder.redirectErrorStream(false);
      Process process = processBuilder.start();

      // 5) 자바 → 파이썬 stdin
      try (BufferedWriter writer = new BufferedWriter(
          new OutputStreamWriter(process.getOutputStream(), StandardCharsets.UTF_8))) {
        writer.write(jsonData);
        writer.flush();
      }

      // 6) 자바 ← 파이썬 stdout
      StringBuilder outBuilder = new StringBuilder();
      try (BufferedReader stdOut = new BufferedReader(
          new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
        String line;
        while ((line = stdOut.readLine()) != null) {
          outBuilder.append(line);
        }
      }

      // 7) 자바 ← 파이썬 stderr
      StringBuilder errBuilder = new StringBuilder();
      try (BufferedReader stdErr = new BufferedReader(
          new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8))) {
        String line;
        while ((line = stdErr.readLine()) != null) {
          errBuilder.append(line).append("\n");
        }
      }

      // 8) 프로세스 종료 대기
      int exitCode = process.waitFor();
      log.info("Python process finished with exit code: {}", exitCode);

      log.info("Python STDOUT: {}", outBuilder);
      log.info("Python STDERR: {}", errBuilder);

      if (exitCode != 0) {
        throw new RuntimeException("Python script exited with code " + exitCode
            + "\n" + errBuilder.toString());
      }

      // 9) 파이썬 결과 JSON 파싱
      String output = outBuilder.toString().trim();
      if (output.isEmpty()) {
        throw new RuntimeException("Python script returned no output");
      }

      Map<String, Object> result = objectMapper.readValue(output, Map.class);
      return result;

    } catch (Exception e) {
      log.error("Error in getSalesForecastByDate(): ", e);
      throw new RuntimeException("예측 실패: " + e.getMessage(), e);
    }
  }
}

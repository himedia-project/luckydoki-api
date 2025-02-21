package com.himedia.luckydokiapi.domain.sales.controller;

import com.himedia.luckydokiapi.domain.sales.service.SalesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/sales")
@Log4j2
@RequiredArgsConstructor
public class SalesController {

  private final SalesService salesService;

  /**
   * /api/sales/forecast
   * 전체 일별 매출 데이터를 이용해 파이썬 스크립트 실행, 결과 반환
   */
  @PostMapping("/forecast")
  public ResponseEntity<?> getSalesForecast() {
    try {
      // Service에서 모든 로직 실행 (DB조회 + Python)
      Map<String, Object> result = salesService.getSalesForecast();
      // 정상 응답
      return ResponseEntity.ok(result);

    } catch (Exception e) {
      log.error("Error in getSalesForecast(): ", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Collections.singletonMap("error", "예측 실패: " + e.getMessage()));
    }
  }

  /**
   * /api/sales/forecast/date
   * 특정 날짜(selectedDate) 매출 데이터를 이용해 파이썬 스크립트 실행, 결과 반환
   */
  @PostMapping("/forecast/date")
  public ResponseEntity<?> getSalesForecastByDate(@RequestBody Map<String, Object> requestBody) {
    try {
      // RequestBody에서 날짜 파라미터 추출
      String selectedDate = (String) requestBody.get("selectedDate");

      // Service 호출
      Map<String, Object> result = salesService.getSalesForecastByDate(selectedDate);
      return ResponseEntity.ok(result);

    } catch (Exception e) {
      log.error("Error in getSalesForecastByDate(): ", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Collections.singletonMap("error", "예측 실패: " + e.getMessage()));
    }
  }
}

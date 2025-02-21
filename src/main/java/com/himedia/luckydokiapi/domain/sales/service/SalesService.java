package com.himedia.luckydokiapi.domain.sales.service;

import com.himedia.luckydokiapi.domain.sales.dto.SalesData;

import java.util.List;
import java.util.Map;

/**
 * DB 조회 + 파이썬 스크립트 실행 로직을 담당하는 인터페이스
 */
public interface SalesService {

  // DB 조회: 전체 일별 매출
  List<SalesData> getDailySalesData();

  // DB 조회: 특정 날짜 매출
  List<SalesData> getSalesDataByDate(String selectedDate);

  // 파이썬 스크립트 실행 (전체 데이터 기반 예측)
  Map<String, Object> getSalesForecast();

  // 파이썬 스크립트 실행 (특정 날짜 데이터 기반 예측)
  Map<String, Object> getSalesForecastByDate(String selectedDate);
}

package com.himedia.luckydokiapi.domain.sales.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class SalesData {

  @JsonProperty("date")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime date;

  @JsonProperty("totalSales")
  private Double totalSales;

  // 생성자: DB에서 받은 java.util.Date를 LocalDate로 변환
  public SalesData(Date date, Double totalSales) {
    this.date = Instant.ofEpochMilli(date.getTime())
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime();
    this.totalSales = totalSales;
  }

  public LocalDateTime getDate() {
    return date;
  }

  public Double getTotalSales() {
    return totalSales;
  }
}

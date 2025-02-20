package com.himedia.luckydokiapi.domain.sales.service;

import com.himedia.luckydokiapi.domain.sales.dto.SalesData;
import com.himedia.luckydokiapi.domain.sales.repository.SalesRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class SalesServiceImpl implements SalesService {

  private final SalesRepository salesRepository;

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

  @Transactional(readOnly = true)
  @Override
  public List<SalesData> getSalesDataByDate(String selectedDate) {
    List<Object[]> rows = salesRepository.findDailySalesNative();
    List<SalesData> salesDataList = new ArrayList<>();
    // 여기서 selectedDate에 해당하는 데이터만 필터링
    for (Object[] row : rows) {
      java.util.Date date = (java.util.Date) row[0];
      String dateStr = new java.text.SimpleDateFormat("yyyy-MM-dd").format(date);
      if (selectedDate.equals(dateStr)) {
        Double totalSales = ((Number) row[1]).doubleValue();
        salesDataList.add(new SalesData(date, totalSales));
      }
    }
    return salesDataList;
  }
}

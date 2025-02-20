package com.himedia.luckydokiapi.domain.sales.service;

import com.himedia.luckydokiapi.domain.sales.dto.SalesData;
import java.util.List;

public interface SalesService {

  List<SalesData> getDailySalesData();

  List<SalesData> getSalesDataByDate(String selectedDate);
}

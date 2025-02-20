package com.himedia.luckydokiapi.domain.sales.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PythonRequest {

  private List<SalesData> salesData;
  private String selectedDate;
}

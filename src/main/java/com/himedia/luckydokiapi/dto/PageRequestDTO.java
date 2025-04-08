package com.himedia.luckydokiapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * abstract class 가능
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public abstract class PageRequestDTO {

  @Builder.Default
  private int page = 1;

  @Builder.Default
  private int size = 10;

  @Builder.Default
  private String sort = "asc";

}

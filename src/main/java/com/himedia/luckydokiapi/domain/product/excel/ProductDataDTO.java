package com.himedia.luckydokiapi.domain.product.excel;



import com.himedia.luckydokiapi.util.excel.ExcelColumn;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@ToString
public class ProductDataDTO {

    @ExcelColumn(header = "상품 번호")
    private Long id;

    @ExcelColumn(header = "제목")
    private String name;

    @ExcelColumn(header = "가격")
    private int price;
    @ExcelColumn(header = "가격")
    private String story;

    @ExcelColumn(header = "할인가격")
    private int discountPrice;

    @ExcelColumn(header = "설명")
    private String description;

    @ExcelColumn(header = "이미지 경로 리스트")
    private String imagePathList;

}

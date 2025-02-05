package com.himedia.luckydokiapi.domain.product.excel;



import com.himedia.luckydokiapi.domain.product.dto.ProductDTO;
import com.himedia.luckydokiapi.domain.product.enums.ProductIsNew;
import com.himedia.luckydokiapi.util.excel.ExcelDataExtractor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class ProductExcelDataExtractor {

    public static List<ProductDTO> extract(MultipartFile file) {
        try (Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(file.getBytes()))) {
            ExcelDataExtractor<ProductDTO> extractor = getExtractor();
            return extractor.extract(workbook.getSheetAt(0));
        } catch (IOException e) {
            throw new RuntimeException("엑셀 파일을 읽는 중 오류가 발생했습니다.", e);
        }
    }

    private static ExcelDataExtractor<ProductDTO> getExtractor() {
        return new ExcelDataExtractor<>() {
            private final DataFormatter dataFormatter = new DataFormatter();
            
            @Override
            protected ProductDTO map(Row row) {
                ProductDTO dto = ProductDTO.builder()
                        .categoryId((long) row.getCell(0).getNumericCellValue())
                        .name(row.getCell(1).getStringCellValue().trim())
                        .price((int) row.getCell(2).getNumericCellValue())
                        .discountPrice((int) row.getCell(5).getNumericCellValue())
                        .description(row.getCell(3).getStringCellValue().trim())
                        .isNew(row.getCell(4).getStringCellValue().trim().equals("Y") ? ProductIsNew.Y : ProductIsNew.N)
                        .tagStrList(Arrays.asList(row.getCell(7).getStringCellValue().split(",")))
                        .imagePathList(getExcelImageList(row.getCell(10).getStringCellValue().trim()))
                        .build();

                validateValue(dto);
                return dto;
            }
        };
    }

    /**
     * 이미지 경로 정보를 ","로 구분하여 List로 반환
     * @param imagePathInfo 이미지 경로 정보
     * @return 이미지 경로 List
     */
    private static List<String> getExcelImageList(String imagePathInfo) {
        List<String> imageList = new ArrayList<>();
        // if "," 없을시
        if(imagePathInfo == null || imagePathInfo.isEmpty()) {
            return imageList;
        }
        if (!imagePathInfo.contains(",")) {
            // 이미지 경로가 1개일 경우
            imageList.add(imagePathInfo);
        } else {
            String[] imagePaths = imagePathInfo.split(",");
            imageList.addAll(Arrays.asList(imagePaths));
        }

        return imageList;
    }

    private static void validateValue(ProductDTO dto) {
        if (dto.getName().length() > 255) {
            throw new IllegalArgumentException("상품 명의 길이가 255자를 초과했습니다.");
        }
//        if (dto.getMainImages().length() > 255) {
//            throw new IllegalArgumentException("게시판 메인 image url의 길이가 255자를 초과했습니다.");
//        }
    }

}

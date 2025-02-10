package com.himedia.luckydokiapi.domain.product.excel;


import com.himedia.luckydokiapi.domain.product.dto.ProductDTO;
import com.himedia.luckydokiapi.util.excel.ExcelGenerator;
import com.himedia.luckydokiapi.util.excel.RegistrationFailResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.himedia.luckydokiapi.util.TimeUtil.getNowTimeStr;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/product/excel")
public class ProductExcelController {

    private final ProductExcelService excelService;


    @PostMapping("/register")
    public ResponseEntity<?> registerByExcel(
            @RequestPart(value = "file") MultipartFile batchRegistrationFile
    ) {
        List<ProductDTO.Request> registrationDtoList = ProductExcelDataExtractor.extract(batchRegistrationFile);
        Long registeredSize = excelService.register(registrationDtoList);
        return new ResponseEntity<>(registeredSize + "개 row 데이터 excel 등록 완료!", HttpStatus.CREATED);
    }


    @PostMapping("/download")
    public ResponseEntity<ByteArrayResource> downloadExcelFile(
            @RequestBody @Valid ProductIdListDTO requestDto
    ) {
        log.info("AdminBrandExcelController downloadBrandFile run...");

        List<ProductDataDTO> dtoList = excelService.getExcelDataList(requestDto);
        ByteArrayResource dataListFile = ExcelGenerator.generateExcelFile(dtoList, "콘텐츠 목록");

        return ExcelGenerator.createResponseEntity(dataListFile, "luckidoki_관리자_콘텐츠_목록_" + getNowTimeStr("yyyyMMddHHmmss") + ".xlsx");
    }


}

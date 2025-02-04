package com.himedia.luckydokiapi.domain.product.excel;


import com.himedia.luckydokiapi.domain.product.dto.ProductDTO;
import com.himedia.luckydokiapi.util.excel.RegistrationFailResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/product/excel")
public class ProductExcelController {

    private final ProductExcelService productExcelService;


    @PostMapping("/register")
    public ResponseEntity<?> registerByExcel(
            @RequestPart(value = "file") MultipartFile batchRegistrationFile
    ) {
        List<ProductDTO> registrationDtoList = ProductExcelDataExtractor.extract(batchRegistrationFile);
        List<RegistrationFailResponseDTO> response = productExcelService.register(registrationDtoList);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


}

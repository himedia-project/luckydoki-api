package com.himedia.luckydokiapi.domain.product.excel;


import com.himedia.luckydokiapi.domain.product.dto.ProductDTO;
import com.himedia.luckydokiapi.domain.product.repository.ProductRepository;
import com.himedia.luckydokiapi.util.excel.RegistrationFailResponseDTO;
import com.himedia.luckydokiapi.util.file.CustomFileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductExcelService {

    private final ProductRepository productRepository;
    private final ProductCreator productCreator;
    private final CustomFileUtil fileUtil;


    public List<RegistrationFailResponseDTO> register(List<ProductDTO> dtoList) {
        List<RegistrationFailResponseDTO> failRowList = new ArrayList<>();
        log.info("register dtoList: {}", dtoList);
        for (int i = 0; i < dtoList.size(); i++) {
            try {
                productCreator.create(dtoList.get(i));
            } catch (IllegalArgumentException e) {
                // 1행 부터 시작이기 때문에 2를 더한다.
                failRowList.add(new RegistrationFailResponseDTO(i + 2, e.getMessage()));
            } catch (Exception e) {
                failRowList.add(new RegistrationFailResponseDTO(i + 2, "Fail" + e.getMessage()));
            }
        }

        return failRowList;
    }



}

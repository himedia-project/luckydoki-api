package com.himedia.luckydokiapi.domain.product.excel;


import com.himedia.luckydokiapi.domain.product.dto.ProductDTO;
import com.himedia.luckydokiapi.domain.product.entity.ProductImage;
import com.himedia.luckydokiapi.domain.product.repository.ProductRepository;
import com.himedia.luckydokiapi.exception.ExcelFailException;
import com.himedia.luckydokiapi.util.excel.RegistrationFailResponseDTO;
import com.himedia.luckydokiapi.util.file.CustomFileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ProductExcelService {

    private final ProductRepository productRepository;
    private final ProductCreator productCreator;
    private final CustomFileUtil fileUtil;


    public Long register(List<ProductDTO.Request> dtoList) {

        log.info("register dtoList: {}", dtoList);
        for (int i = 0; i < dtoList.size(); i++) {
            try {
                productCreator.create(dtoList.get(i));
            } catch (IllegalArgumentException e) {
                // 1행 부터 시작이기 때문에 2를 더한다.
                throw new ExcelFailException(i+2 +"행에서 문제발생, " + e.getMessage());
            } catch (Exception e) {
                throw new ExcelFailException(i+2 +"행에서 Fail문제 발생, " +e.getMessage());
            }
        }

        return (long) dtoList.size();
    }


    public List<ProductDataDTO> getExcelDataList(ProductIdListDTO requestDto) {
        return productRepository.findByIdList(requestDto.getIdList()).stream()
                .map(product -> {
                    return ProductDataDTO.builder()
                            .id(product.getId())
                            .name(product.getName())
                            .price(product.getPrice())
                            .discountPrice(product.getDiscountPrice())
                            .description(product.getDescription())
                            .stockNumber(product.getStockNumber())
                            .imagePathList(fileUtil.getMergedS3ImagePathList(product.getImageList().stream().map(ProductImage::getImageName).toList()))
                            .build();
                })
                .toList();
    }
}

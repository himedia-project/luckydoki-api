package com.himedia.luckydokiapi.domain.product.excel;

import com.himedia.luckydokiapi.domain.product.dto.ProductDTO;
import com.himedia.luckydokiapi.domain.product.service.AdminProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductCreator {

    private final AdminProductService productService;

    public void create(ProductDTO.Request dto) {

        productService.register(dto);

    }

}

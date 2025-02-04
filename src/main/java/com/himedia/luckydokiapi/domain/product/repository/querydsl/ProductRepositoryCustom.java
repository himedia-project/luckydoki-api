package com.himedia.luckydokiapi.domain.product.repository.querydsl;




import com.himedia.luckydokiapi.domain.product.dto.ProductRequestDTO;
import com.himedia.luckydokiapi.domain.product.entity.Product;

import java.util.List;

public interface ProductRepositoryCustom {


    List<Product> findByDTO(ProductRequestDTO requestDTO);
}

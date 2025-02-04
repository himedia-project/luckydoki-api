package com.himedia.luckydokiapi.domain.product.repository.querydsl;




import com.himedia.luckydokiapi.domain.product.dto.ProductRequestDTO;
import com.himedia.luckydokiapi.domain.product.entity.Product;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductRepositoryCustom {

    Page<Product> findListBy(ProductRequestDTO requestDTO);

    List<Product> findByDTO(ProductRequestDTO requestDTO);
}

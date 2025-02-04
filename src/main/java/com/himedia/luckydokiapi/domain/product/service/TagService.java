package com.himedia.luckydokiapi.domain.product.service;



import com.himedia.luckydokiapi.domain.product.dto.ProductDTO;

import java.util.List;

public interface TagService {

    List<ProductDTO> list(Long id);
}

package com.himedia.luckydokiapi.domain.heart.repository.querydsl;



import com.himedia.luckydokiapi.domain.product.entity.Product;

import java.util.List;

public interface HeartRepositoryCustom {

    List<Product> findProductListByMember(String email);
}

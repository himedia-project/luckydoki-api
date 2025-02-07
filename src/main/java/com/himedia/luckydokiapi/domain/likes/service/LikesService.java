package com.himedia.luckydokiapi.domain.likes.service;

import com.himedia.luckydokiapi.domain.likes.dto.LikesProductDTO;
import com.himedia.luckydokiapi.domain.likes.entity.ProductLike;
import com.himedia.luckydokiapi.domain.product.entity.Product;
import com.himedia.luckydokiapi.domain.shop.entity.Shop;

import java.util.List;

public interface LikesService {
    Boolean changeLikesProduct(String email, Long productId);

    List<LikesProductDTO> getProductLikesByMember(String email);


    default LikesProductDTO EntityToDTO(ProductLike productLike) {
        LikesProductDTO likesProductDTO = LikesProductDTO.builder()
                .id(productLike.getId())
                .productId(productLike.getProduct().getId())
                .email(productLike.getMember().getEmail())
                .productName(productLike.getProduct().getName())
                .productCode(productLike.getProduct().getCode())
                .build();

        return likesProductDTO;
    }
}

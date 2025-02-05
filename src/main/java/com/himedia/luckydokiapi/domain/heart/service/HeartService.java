package com.himedia.luckydokiapi.domain.heart.service;


import com.himedia.luckydokiapi.domain.heart.dto.HeartDTO;
import com.himedia.luckydokiapi.domain.heart.entity.Heart;
import com.himedia.luckydokiapi.domain.product.dto.ProductResponseDTO;

import java.util.List;
public interface HeartService {


    /**
     * 회원의 찜 상품 리스트 조회
     * @param email 회원 이메일
     * @return 찜 상품 리스트
     */
    List<ProductResponseDTO> findProductListByMember(String email);


    /**
     * 상품 찜하기/찜 취소
     * @param productId 상품 ID
     * @param email 회원 이메일
     */
    void heartProduct(Long productId, String email);



    default HeartDTO entityToDTO(Heart heart) {
        return HeartDTO.builder()
                .heartId(heart.getId())
                .email(heart.getMember().getEmail())
                .productId(heart.getProduct().getId())
                .contentId(null)
                .build();
    }
}

package com.himedia.luckydokiapi.domain.product.repository.querydsl;


import com.himedia.luckydokiapi.domain.product.dto.ProductSearchDTO;
import com.himedia.luckydokiapi.domain.product.entity.Product;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductRepositoryCustom {

    List<Product> findByIdList(List<Long> idList);

    Page<Product> findListBy(ProductSearchDTO requestDTO);

    List<Product> findByDTO(ProductSearchDTO requestDTO);

    List<Product> findProductByShopMemberEmail(String email);

    List<Product> findByProductCategoryId(Long CategoryId);

    List<Product> findTop10ByOrderByLikeCountAndReviewCountDesc();

    List<Product> findRecommendFirstExtractList(String email);

    /**
     * 특정 시간 이후에 변경된 상품 ID 목록을 조회합니다.
     * @param fromTime 이 시간 이후로 변경된 상품을 조회
     * @return 변경된 상품 ID 목록
     */
    List<Long> getRecentlyChangedProducts(LocalDateTime fromTime);

    /**
     * 특정 시간 이후에 추가된 상품 ID 목록을 조회합니다.
     * @param fromTime 이 시간 이후로 추가된 상품을 조회
     * @return 추가된 상품 ID 목록
     */
    List<Long> getRecentlyAddedProducts(LocalDateTime fromTime);
}

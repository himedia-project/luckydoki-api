package com.himedia.luckydokiapi.domain.shop.service;

import com.himedia.luckydokiapi.domain.product.dto.ProductDTO;
import com.himedia.luckydokiapi.domain.shop.dto.ShopResponseDTO;
import com.himedia.luckydokiapi.domain.shop.dto.ShopProductResponseDTO;
import com.himedia.luckydokiapi.domain.shop.entity.Shop;
import com.himedia.luckydokiapi.domain.shop.repository.ShopRepository;
import com.himedia.luckydokiapi.domain.likes.repository.ProductLikeRepository;
import com.himedia.luckydokiapi.domain.likes.repository.ShopLikeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShopServiceImpl implements ShopService {

    private final ShopRepository shopRepository;
    private final ProductLikeRepository productLikeRepository;
    private final ShopLikeRepository shopLikeRepository;

    // ✅ 특정 샵(셀러) 정보 조회 (좋아요 여부 반영)
    @Override
    @Transactional(readOnly = true)
    public ShopResponseDTO getShopProfileById(Long shopId, String email) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID를 가진 샵이 존재하지 않습니다: " + shopId));

        boolean likes = (email != null) && shopLikeRepository.existsByMember_EmailAndShop_Id(email, shopId);

        return ShopResponseDTO.builder()
                .id(shop.getId())
                .image(shop.getImage()) // S3 저장된 샵 이미지
                .introduction(shop.getIntroduction()) // 샵 소개글
                .email(shop.getMember().getEmail())
                .nickName(shop.getMember().getNickName())
                .likes(likes)
                .build();
    }

    // ✅ 특정 샵(셀러)의 상품 리스트 조회 (좋아요 여부 반영)
    @Override
    @Transactional(readOnly = true)
    public ShopProductResponseDTO getShopProducts(Long shopId, String email) {
        Shop shop = shopRepository.findByIdWithProducts(shopId)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID를 가진 샵이 존재하지 않습니다: " + shopId));

        List<ProductDTO.Response> productDTOList = shop.getProductList().stream()
                .map(product -> {
                    boolean likes = (email != null) && productLikeRepository.existsByMember_EmailAndProduct_Id(email, product.getId());

                    return ProductDTO.Response.builder()
                            .id(product.getId())
                            .code(product.getCode())
                            .categoryId(product.getCategory().getId())
                            .categoryName(product.getCategory().getName())
                            .name(product.getName())
                            .price(product.getPrice())
                            .discountPrice(product.getDiscountPrice())
                            .discountRate(product.getDiscountRate())
                            .description(product.getDescription())
                            .isNew(product.getIsNew())
                            .best(product.getBest())
                            .event(product.getEvent())
                            .shopId(shop.getId())
                            .shopName(shop.getMember().getNickName())
                            .shopImage(shop.getImage())
                            .stockNumber(product.getStockNumber())
                            .nickName(shop.getMember().getNickName())
                            .email(shop.getMember().getEmail())
                            .uploadFileNames(product.getImageList().stream().map(image -> image.getImageName()).toList())
                            .tagStrList(product.getProductTagList().stream().map(tag -> tag.getTag().getName()).toList())
                            .createdAt(product.getCreatedAt())
                            .modifiedAt(product.getModifiedAt())
                            .likes(likes)
                            .build();
                }).toList();

        return ShopProductResponseDTO.builder()
                .shopId(shop.getId())
                .shopName(shop.getMember().getNickName())
                .productList(productDTOList)
                .build();
    }
}

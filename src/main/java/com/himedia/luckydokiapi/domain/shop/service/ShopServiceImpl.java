package com.himedia.luckydokiapi.domain.shop.service;

import com.himedia.luckydokiapi.domain.product.dto.ProductDTO;
import com.himedia.luckydokiapi.domain.shop.dto.ShopResponseDTO;
import com.himedia.luckydokiapi.domain.shop.entity.Shop;
import com.himedia.luckydokiapi.domain.shop.repository.ShopRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShopServiceImpl implements ShopService {

    private final ShopRepository shopRepository;

    @Override
    @Transactional(readOnly = true)
    public ShopResponseDTO getShopProfileById(Long shopId) {
        Shop shop = shopRepository.findByIdWithProducts(shopId)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID를 가진 샵이 존재하지 않습니다: " + shopId));

        // 기존 ProductDTO.Response를 활용하여 변환
        List<ProductDTO.Response> productDTOList = shop.getProductList().stream()
                .map(product -> ProductDTO.Response.builder()
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
                        .uploadFileNames(product.getImageList().stream().map(image -> image.getImageName()).toList()) // 이미지 리스트 변환
                        .tagStrList(product.getProductTagList().stream().map(tag -> tag.getTag().getName()).toList()) // 태그 리스트 변환
                        .createdAt(product.getCreatedAt())
                        .modifiedAt(product.getModifiedAt())
                        .build()
                ).toList();

        return ShopResponseDTO.builder()
                .id(shop.getId())
                .image(shop.getImage()) // S3 저장된 샵 이미지
                .introduction(shop.getIntroduction()) // 샵 소개글
                .email(shop.getMember().getEmail())
                .nickName(shop.getMember().getNickName())
                .productList(productDTOList) // 상품 리스트 포함
                .build();
    }
}

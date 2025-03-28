package com.himedia.luckydokiapi.domain.likes.service;

import com.himedia.luckydokiapi.domain.likes.dto.LikesProductDTO;
import com.himedia.luckydokiapi.domain.likes.dto.LikesShopDTO;
import com.himedia.luckydokiapi.domain.likes.entity.ProductLike;
import com.himedia.luckydokiapi.domain.likes.entity.ShopLike;
import com.himedia.luckydokiapi.domain.product.entity.Product;
import com.himedia.luckydokiapi.domain.product.entity.ProductImage;

import java.util.List;

public interface LikesService {
    Boolean changeLikesProduct(String email, Long productId);

    List<LikesProductDTO> getProductLikesByMember(String email);


    default LikesProductDTO entityToDTO(ProductLike productLike) {
        Product product = productLike.getProduct();
        List<ProductImage> imageList = product.getImageList();

        if (imageList == null || imageList.isEmpty()) {
            return null;
        }
        List<String> fileNameList = imageList.stream().map(ProductImage::getImageName).toList();

        LikesProductDTO likesProductDTO = LikesProductDTO.builder()
                .id(productLike.getId())
                .productId(productLike.getProduct().getId())
                .email(productLike.getMember().getEmail())
                .productName(productLike.getProduct().getName())
                .productCode(productLike.getProduct().getCode())
                .discountRate(product.getDiscountRate())
                .price(productLike.getProduct().getPrice())
                .discountPrice(productLike.getProduct().getDiscountPrice())
                .likesCount(product.productLikesCount(productLike))
                .reviewAverage(product.getReviewAverage())
                .reviewCount(product.getReviewCount())
                .isNew(product.getIsNew())
                .best(product.getBest())
                .event(product.getEvent())
                .likes(true)
                .productImageUrl(fileNameList.get(0))
                .build();
        return likesProductDTO;
    }

    Boolean changeLikesShop(String email, Long shopId);

    List<LikesShopDTO> getShopLikesByMember(String email);

    default LikesShopDTO EntityToDTOShop(ShopLike shopLike) {
        LikesShopDTO likesShopDTO = LikesShopDTO.builder()
                .id(shopLike.getId())
                .email(shopLike.getMember().getEmail())
                .shopId(shopLike.getShop().getId())
                .shopImageUrl(shopLike.getShop().getImage())
                .sellerNickname(shopLike.getShop().getMember().getNickName())
                .sellerEmail(shopLike.getShop().getMember().getEmail())
                .likes(true)
                .build();
        return likesShopDTO;

    }
}

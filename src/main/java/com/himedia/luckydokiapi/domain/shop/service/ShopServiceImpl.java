package com.himedia.luckydokiapi.domain.shop.service;

import com.himedia.luckydokiapi.domain.community.dto.CommunityResponseDTO;
import com.himedia.luckydokiapi.domain.likes.repository.ProductLikeRepository;
import com.himedia.luckydokiapi.domain.likes.repository.ShopLikeRepository;
import com.himedia.luckydokiapi.domain.product.dto.ProductDTO;
import com.himedia.luckydokiapi.domain.product.repository.ProductRepository;
import com.himedia.luckydokiapi.domain.shop.dto.ShopCommunityResponseDTO;
import com.himedia.luckydokiapi.domain.shop.dto.ShopProductResponseDTO;
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
    private final ProductLikeRepository productLikeRepository;
    private final ShopLikeRepository shopLikeRepository;
    private final ProductRepository productRepository;

    // 특정 샵(셀러) 정보 조회 (좋아요 여부 반영)
    @Override
    @Transactional(readOnly = true)
    public ShopResponseDTO getShopProfileById(Long shopId, String email) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID를 가진 샵이 존재하지 않습니다: " + shopId));

        boolean likes = (email != null) && shopLikeRepository.likes(email, shopId);

        return ShopResponseDTO.builder()
                .id(shop.getId())
                .image(shop.getImage()) // S3 저장된 샵 이미지
                .introduction(shop.getIntroduction()) // 샵 소개글
                .email(shop.getMember().getEmail())
                .nickName(shop.getMember().getNickName())
                .likes(likes)
                .build();
    }

    // 특정 샵(셀러)의 상품 리스트 조회 (좋아요 여부 반영)
    @Override
    @Transactional(readOnly = true)
    public ShopProductResponseDTO getShopProducts(Long shopId, String email) {

        Shop shop = shopRepository.findByIdWithProducts(shopId)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID를 가진 샵이 존재하지 않습니다: " + shopId));

        List<ProductDTO.Response> productDTOList = productRepository.findByShopId(shopId).stream()
                .map(product -> {
                    boolean likes = (email != null) && productLikeRepository.likes(email, product.getId());
                    ProductDTO.Response response = ProductDTO.Response.from(product);
                    response.setLikes(likes);
                    return response;
                }).toList();

        return ShopProductResponseDTO.builder()
                .shopId(shop.getId())
                .shopName(shop.getMember().getNickName())
                .productList(productDTOList)
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public ShopCommunityResponseDTO getShopCommunities(Long shopId, String email) {
        Shop shop = shopRepository.findByIdWithCommunities(shopId)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID를 가진 샵이 존재하지 않습니다: " + shopId));
//        shop.getCommunityList() -> DTO mapping
        List<CommunityResponseDTO> communityDTOList = shop.getMember().getCommunityList().stream()
                .map(CommunityResponseDTO::from).toList();

        return ShopCommunityResponseDTO.builder()
                .shopId(shop.getId())
                .shopName(shop.getMember().getNickName())
                .communityList(communityDTOList)
                .build();
    }

}

package com.himedia.luckydokiapi.domain.likes.service;


import com.himedia.luckydokiapi.domain.likes.dto.LikesProductDTO;
import com.himedia.luckydokiapi.domain.likes.dto.LikesShopDTO;
import com.himedia.luckydokiapi.domain.likes.entity.ProductLike;
import com.himedia.luckydokiapi.domain.likes.entity.ShopLike;
import com.himedia.luckydokiapi.domain.likes.repository.ProductLikeRepository;
import com.himedia.luckydokiapi.domain.likes.repository.ShopLikeRepository;
import com.himedia.luckydokiapi.domain.member.entity.Member;
import com.himedia.luckydokiapi.domain.member.repository.MemberRepository;
import com.himedia.luckydokiapi.domain.product.entity.Product;
import com.himedia.luckydokiapi.domain.product.repository.ProductRepository;
import com.himedia.luckydokiapi.domain.shop.entity.Shop;
import com.himedia.luckydokiapi.domain.shop.repository.ShopRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class LikesServiceImpl implements LikesService {

    private final MemberRepository memberRepository;
    private final ProductLikeRepository productLikeRepository;
    private final ShopLikeRepository shopLikeRepository;
    private final ProductRepository productRepository;
    private final ShopRepository shopRepository;


    @Override
    public Boolean changeLikesProduct(String email, Long productId) {
        log.info("productId: {}", productId);
        Member member = getMember(email);
        Product product = getProduct(productId);
        Boolean isLike = productLikeRepository.existsByMember_EmailAndProduct_Id(member.getEmail(), product.getId());
        if (isLike) {
            productLikeRepository.deleteByEmailAndProductId(member.getEmail(), product.getId());
            return false;
        } //좋아요 취소
        ProductLike productLike = ProductLike.builder()
                .product(product)
                .member(member)
                .build();
        productLikeRepository.save(productLike);
        return true; // 좋아요 추가 !
    }

    @Transactional(readOnly = true)
    @Override
    public List<LikesProductDTO> getProductLikesByMember(String email) {
        Member member = getMember(email);
        List<LikesProductDTO> likesList = productLikeRepository.findByEmail(member.getEmail()).stream().map(this::EntityToDTO).toList();
        return likesList;
    }

    @Override
    public Boolean changeLikesShop(String email, Long shopId) {
        Member member = getMember(email);
        Shop shop = getShop(shopId);
        Boolean isLike = shopLikeRepository.existsByMember_EmailAndShop_Id(member.getEmail(), shop.getId());
        if (isLike) {
            shopLikeRepository.deleteByEmailAndShopId(member.getEmail(), shop.getId());
            return false;
        } //좋아요 취소
        ShopLike shopLike = ShopLike.builder()
                .shop(shop)
                .member(member)
                .build();
        shopLikeRepository.save(shopLike);
        return true; // 좋아요 추가 !
    }

    @Transactional(readOnly = true)
    @Override
    public List<LikesShopDTO> getShopLikesByMember(String email) {
        Member member = getMember(email);
        List<LikesShopDTO> likesList = shopLikeRepository.findByEmail(member.getEmail()).stream().map(this::EntityToDTOShop).toList();
        return likesList;
    }

    private Member getMember(String email) {
        return memberRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다"));
    }

    private Product getProduct(Long productId) {
        return productRepository.findById(productId).orElseThrow(() -> new EntityNotFoundException("해당 싱품은 존재하지 않습니다 "));
    }

    private Shop getShop(Long shopId) {
        return shopRepository.findById(shopId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 셀러입니다"));
    }
}

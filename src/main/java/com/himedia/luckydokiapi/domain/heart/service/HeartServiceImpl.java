package com.himedia.luckydokiapi.domain.heart.service;


import com.himedia.luckydokiapi.domain.heart.entity.Heart;
import com.himedia.luckydokiapi.domain.heart.repository.HeartRepository;
import com.himedia.luckydokiapi.domain.member.entity.Member;
import com.himedia.luckydokiapi.domain.member.service.MemberService;
import com.himedia.luckydokiapi.domain.product.dto.ProductResponseDTO;
import com.himedia.luckydokiapi.domain.product.entity.Product;
import com.himedia.luckydokiapi.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class HeartServiceImpl implements HeartService {

    private final HeartRepository heartRepository;

    private final ProductService productService;

    private final MemberService memberService;

    @Override
    public void heartProduct(Long productId, String email) {
        // productId & email
        Product product = this.productService.getEntity(productId);
        Member member = this.memberService.getEntity(email);


        Optional<Heart> heartProduct = heartRepository.findHeartProduct(email, productId);
        if (heartProduct.isPresent()) {
            // heart o -> heart 삭제
            heartRepository.delete(heartProduct.get());
        } else {
            //   heart x -> heart 생성
            Heart heart = Heart.builder().member(member)
                    .product(product)
//                    .content(null)
                    .build();
            heartRepository.save(heart);
        }

    }


    @Transactional(readOnly = true)
    @Override
    public List<ProductResponseDTO> findProductListByMember(String email) {
        return heartRepository.findProductListByMember(email).stream()
                .map(this.productService::entityToDTO)
                .collect(Collectors.toList());
    }





}

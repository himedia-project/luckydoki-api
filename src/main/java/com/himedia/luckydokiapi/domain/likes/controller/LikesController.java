package com.himedia.luckydokiapi.domain.likes.controller;


import com.himedia.luckydokiapi.domain.likes.dto.LikesProductDTO;
import com.himedia.luckydokiapi.domain.likes.dto.LikesRequestDTO;
import com.himedia.luckydokiapi.domain.likes.dto.LikesShopDTO;
import com.himedia.luckydokiapi.domain.likes.service.LikesService;
import com.himedia.luckydokiapi.security.MemberDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
public class LikesController {

    private final LikesService likesService;

    @PostMapping("/product")
    //final: 인증객체 불변성 , 참조 방지
    public ResponseEntity<Boolean> toggleProductLikes(@AuthenticationPrincipal final MemberDTO memberDTO, @RequestBody LikesRequestDTO likesRequestDTO) {
        log.info("likesRequestDTO : {}", likesRequestDTO);
        Boolean exist = likesService.changeLikesProduct(memberDTO.getEmail(), likesRequestDTO.getProductId());
        return ResponseEntity.ok(exist);
    }

    @GetMapping("/product/list")
    public ResponseEntity<List<LikesProductDTO>> getProductLikes(@AuthenticationPrincipal final MemberDTO memberDTO) {
        log.info("likes memberDTO : {}", memberDTO);
        List<LikesProductDTO> likesProductDTOS = likesService.getProductLikesByMember(memberDTO.getEmail());
        return ResponseEntity.ok(likesProductDTOS);
    }

    @PostMapping("/shop")
    //final: 인증객체 불변성 , 참조 방지
    public ResponseEntity<Boolean> toggleShopLikes(@AuthenticationPrincipal final MemberDTO memberDTO, @RequestBody LikesRequestDTO likesRequestDTO) {
        log.info("likesRequestDTO : {}", likesRequestDTO);
        Boolean exist = likesService.changeLikesShop(memberDTO.getEmail(), likesRequestDTO.getShopId());
        return ResponseEntity.ok(exist);
    }

    @GetMapping("/shop/list")
    public ResponseEntity<List<LikesShopDTO>> getShopLikes(@AuthenticationPrincipal final MemberDTO memberDTO) {
        log.info("likes memberDTO : {}", memberDTO);
        List<LikesShopDTO> likesShopDTOS = likesService.getShopLikesByMember(memberDTO.getEmail());
        return ResponseEntity.ok(likesShopDTOS);
    }
}

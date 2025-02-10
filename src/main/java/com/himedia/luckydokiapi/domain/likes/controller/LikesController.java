package com.himedia.luckydokiapi.domain.likes.controller;


import com.himedia.luckydokiapi.domain.likes.dto.LikesProductDTO;
import com.himedia.luckydokiapi.domain.likes.dto.LikesRequestDTO;
import com.himedia.luckydokiapi.domain.likes.dto.LikesShopDTO;
import com.himedia.luckydokiapi.domain.likes.service.LikesService;
import com.himedia.luckydokiapi.security.MemberDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
public class LikesController {

    private final LikesService likesService;

    @PostMapping("/product")
    //final: 인증객체 불변성 , 참조 방지
    public ResponseEntity<String> toggleProductLikes(@AuthenticationPrincipal final MemberDTO memberDTO, @RequestBody LikesRequestDTO likesRequestDTO) {
      Boolean exist=  likesService.changeLikesProduct(memberDTO.getEmail(),likesRequestDTO.getProductId());
       return ResponseEntity.ok(exist ? "좋아요추가" :"좋아요 취소");
    }

    @GetMapping("/product")
    public ResponseEntity<List<LikesProductDTO>> getProductLikes(@AuthenticationPrincipal final MemberDTO memberDTO){
        List<LikesProductDTO> likesProductDTOS = likesService.getProductLikesByMember(memberDTO.getEmail());
        return ResponseEntity.ok(likesProductDTOS);
    }

    @PostMapping("/shop")
    //final: 인증객체 불변성 , 참조 방지
    public ResponseEntity<String> toggleShopLikes(@AuthenticationPrincipal final MemberDTO memberDTO, @RequestBody LikesRequestDTO likesRequestDTO) {
        Boolean exist=  likesService.changeLikesShop(memberDTO.getEmail(),likesRequestDTO.getShopId());
        return ResponseEntity.ok(exist ? "좋아요추가" :"좋아요 취소");
    }

    @GetMapping("/shop")
    public ResponseEntity<List<LikesShopDTO>> getShopLikes(@AuthenticationPrincipal final MemberDTO memberDTO){
        List<LikesShopDTO> likesShopDTOS = likesService.getShopLikesByMember(memberDTO.getEmail());
        return ResponseEntity.ok(likesShopDTOS);
    }
}

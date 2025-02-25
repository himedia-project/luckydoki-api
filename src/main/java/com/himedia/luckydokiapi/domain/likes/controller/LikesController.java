package com.himedia.luckydokiapi.domain.likes.controller;


import com.himedia.luckydokiapi.domain.chat.dto.ChatRoomDTO;
import com.himedia.luckydokiapi.domain.likes.dto.LikesProductDTO;
import com.himedia.luckydokiapi.domain.likes.dto.LikesRequestDTO;
import com.himedia.luckydokiapi.domain.likes.dto.LikesShopDTO;
import com.himedia.luckydokiapi.domain.likes.service.LikesService;
import com.himedia.luckydokiapi.security.MemberDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "likes - api ", description = "product , shop 좋아요 상태 변경 / 좋아요 리스트 조회 api")
public class LikesController {

    private final LikesService likesService;

    @PostMapping("/product")
    @Operation(
            summary = "product 좋아요 상태 변경",
            description = "좋아요 버튼 클릭 시 호출되는 API이며 이미 좋아요를 누른 경우(true) false로 변환되는 (좋아요 취소) 토글 형식 API입니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "likes 상태 변경 값들, 둘 중 하나가 null이여도 실행됩니다",
                    required = true,
                    content = @Content(schema = @Schema(implementation = LikesRequestDTO.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "좋아요 상태 변경 완료",
                            content = @Content(schema = @Schema(type = "boolean", example = "true", description = "true: 좋아요 상태로 변경, false: 좋아요 취소 상태로 변경"))
                    )
            }
    )
    public ResponseEntity<Boolean> toggleProductLikes(@Parameter(description = "인증된 사용자 정보", hidden = true)
                                                      @AuthenticationPrincipal final MemberDTO memberDTO,
                                                      @RequestBody LikesRequestDTO likesRequestDTO) {
        log.info("likesRequestDTO : {}", likesRequestDTO);
        Boolean exist = likesService.changeLikesProduct(memberDTO.getEmail(), likesRequestDTO.getProductId());
        return ResponseEntity.ok(exist);
    }
    @Operation(
            summary = "좋아요한 상품 목록 조회",
            description = "로그인한 사용자가 좋아요를 누른 상품 목록을 조회합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = LikesProductDTO.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "인증되지 않은 사용자",
                            content = @Content
                    )
            }
    )
    @GetMapping("/product/list")
    public ResponseEntity<List<LikesProductDTO>> getProductLikes(@Parameter(description = "인증된 사용자 정보", hidden = true)
                                                                 @AuthenticationPrincipal final MemberDTO memberDTO) {
        log.info("likes memberDTO : {}", memberDTO);
        List<LikesProductDTO> likesProductDTOS = likesService.getProductLikesByMember(memberDTO.getEmail());
        return ResponseEntity.ok(likesProductDTOS);
    }

    @PostMapping("/shop")
    public ResponseEntity<Boolean> toggleShopLikes(@Parameter(description = "인증된 사용자 정보", hidden = true)
                                                   @AuthenticationPrincipal final MemberDTO memberDTO, @RequestBody LikesRequestDTO likesRequestDTO) {
        log.info("likesRequestDTO : {}", likesRequestDTO);
        Boolean exist = likesService.changeLikesShop(memberDTO.getEmail(), likesRequestDTO.getShopId());
        return ResponseEntity.ok(exist);
    }

    @GetMapping("/shop/list")
    public ResponseEntity<List<LikesShopDTO>> getShopLikes(@Parameter(description = "인증된 사용자 정보", hidden = true)
                                                           @AuthenticationPrincipal final MemberDTO memberDTO) {
        log.info("likes memberDTO : {}", memberDTO);
        List<LikesShopDTO> likesShopDTOS = likesService.getShopLikesByMember(memberDTO.getEmail());
        return ResponseEntity.ok(likesShopDTOS);
    }
}

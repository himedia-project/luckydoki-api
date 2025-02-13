package com.himedia.luckydokiapi.domain.community.service;

import com.himedia.luckydokiapi.domain.community.dto.CommunityRequestDTO;
import com.himedia.luckydokiapi.domain.community.dto.CommunityResponseDTO;
import com.himedia.luckydokiapi.domain.community.dto.CommunitySearchDTO;
import com.himedia.luckydokiapi.domain.community.entity.Community;
import com.himedia.luckydokiapi.domain.community.entity.CommunityProduct;
import com.himedia.luckydokiapi.domain.community.repository.CommunityRepository;
import com.himedia.luckydokiapi.domain.member.entity.Member;
import com.himedia.luckydokiapi.domain.member.repository.MemberRepository;
import com.himedia.luckydokiapi.domain.member.service.MemberService;
import com.himedia.luckydokiapi.domain.product.entity.Product;
import com.himedia.luckydokiapi.domain.product.repository.ProductRepository;
import com.himedia.luckydokiapi.util.file.CustomFileUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CommunityServiceImpl implements CommunityService {

    private final CommunityRepository communityRepository;
    private final MemberService memberService;
    private final CustomFileUtil fileUtil;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<CommunityResponseDTO> getAllCommunities(CommunitySearchDTO request) {
        log.info("getAllCommunities 요청: {}", request);

        List<Community> communities = communityRepository.findByDTO(request);
        log.info("조회된 게시글 개수: {}", communities.size());

        return communities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<CommunityResponseDTO> getCommunitiesByMemberEmail(String email) {
        return communityRepository.findByMemberEmail(email)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CommunityResponseDTO getCommunityById(Long communityId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new EntityNotFoundException("해당 게시글이 존재하지 않습니다."));
        return toDTO(community);
    }


    @Override
    public CommunityResponseDTO postCommunity(String email, CommunityRequestDTO request) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        // ✅ 제목과 내용 필수 검증
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("제목은 필수 입력 항목입니다.");
        }
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("내용은 필수 입력 항목입니다.");
        }

        Community community = Community.builder()
                .member(member)
                .title(request.getTitle()) // 제목 저장
                .content(request.getContent())
                .build();

        communityRepository.save(community);

        // 이미지 업로드 (선택 사항)
        if (request.getFiles() != null && !request.getFiles().isEmpty()) {
            List<String> uploadedUrls = fileUtil.uploadS3Files(request.getFiles());
            request.setUploadFileNames(uploadedUrls);
        }

        // 사용자가 등록한 상품인지 검증 후 추가
        if (request.getProductIds() != null && !request.getProductIds().isEmpty()) {
            List<Product> userProducts = productRepository.findProductByShopMemberEmail(email);
            List<Long> userProductIds = userProducts.stream().map(Product::getId).toList();

            for (Long productId : request.getProductIds()) {
                if (!userProductIds.contains(productId)) {
                    throw new IllegalArgumentException("본인이 등록한 상품만 게시물에 추가할 수 있습니다.");
                }

                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new RuntimeException("존재하지 않는 상품입니다."));

                CommunityProduct communityProduct = CommunityProduct.builder()
                        .community(community)
                        .product(product)
                        .build();

                community.getCommunityProductList().add(communityProduct);
            }
        }

        communityRepository.save(community);
        return new CommunityResponseDTO(community);
    }

//    @Override
//    public CommunityResponseDTO updateCommunity(Long communityId, String email, CommunityRequestDTO request) {
//        Community community = communityRepository.findById(communityId)
//                .orElseThrow(() -> new EntityNotFoundException("해당 게시글이 존재하지 않습니다."));
//
//        Member member = memberRepository.findByEmail(email)
//                .orElseThrow(() -> new RuntimeException("사용자 없음"));
//
//        if (!community.getMember().getEmail().equals(member.getEmail())) {
//            throw new IllegalArgumentException("본인의 게시글만 수정할 수 있습니다.");
//        }
//
//        // 제목과 내용 필수 검증 추가
//        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
//            throw new IllegalArgumentException("제목은 필수 입력 항목입니다.");
//        }
//        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
//            throw new IllegalArgumentException("내용은 필수 입력 항목입니다.");
//        }
//
//        // 게시글 제목 및 내용 업데이트
//        community.setTitle(request.getTitle());
//        community.setContent(request.getContent());
//
//        // 이미지 업데이트 (기존 이미지 삭제 후 새 이미지 추가)
//        community.getImageList().clear();
//        if (request.getFiles() != null && !request.getFiles().isEmpty()) {
//            List<String> uploadedUrls = fileUtil.uploadS3Files(request.getFiles());
//            request.setUploadFileNames(uploadedUrls);
//        }
//
//        if (request.getUploadFileNames() != null && !request.getUploadFileNames().isEmpty()) {
//            int order = 1;
//            for (String imageUrl : request.getUploadFileNames()) {
//                CommunityImage communityImage = CommunityImage.builder()
//                        .community(community)
//                        .imageName(imageUrl)
//                        .ord(order++)
//                        .build();
//                community.getImageList().add(communityImage);
//            }
//        }
//
//        // ✅ 기존 상품 삭제
//        community.getCommunityProductList().clear();
//
//        // ✅ 사용자가 등록한 상품인지 검증 후 추가
//        if (request.getProductIds() != null && !request.getProductIds().isEmpty()) {
//            List<Product> userProducts = productRepository.findProductByShopMemberEmail(email);
//            List<Long> userProductIds = userProducts.stream().map(Product::getId).toList();
//
//            for (Long productId : request.getProductIds()) {
//                if (!userProductIds.contains(productId)) {
//                    throw new IllegalArgumentException("본인이 등록한 상품만 게시물에 추가할 수 있습니다.");
//                }
//
//                Product product = productRepository.findById(productId)
//                        .orElseThrow(() -> new RuntimeException("존재하지 않는 상품입니다."));
//
//                CommunityProduct communityProduct = CommunityProduct.builder()
//                        .community(community)
//                        .product(product)
//                        .build();
//
//                community.getCommunityProductList().add(communityProduct);
//            }
//        }
//
//        communityRepository.save(community);
//        return new CommunityResponseDTO(community);
//    }



    public void deleteCommunity(Long communityId, String email) {
        log.info(" 게시글 삭제 요청: communityId={}, email={}", communityId, email);
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new EntityNotFoundException("해당 게시글이 존재하지 않습니다."));

        Member member = this.getMember(email);
        if (!community.getMember().getEmail().equals(member.getEmail())) {
            throw new IllegalArgumentException("본인의 게시글만 삭제할 수 있습니다.");
        }

        communityRepository.delete(community);

        // 등록된 상품(중간매필테이블) 삭제
        // 1 - [1,2,3,4]
        // 1 - 1
        // 1 - 2
        // 1 - 3
        // 1 - 4
        log.info(" 게시글 삭제 완료: communityId={}", communityId);
    }




    private Member getMember(String email) {
        return memberService.getEntity(email);
    }
}

package com.himedia.luckydokiapi.domain.community.service;

import com.himedia.luckydokiapi.domain.community.dto.CommunityRequestDTO;
import com.himedia.luckydokiapi.domain.community.dto.CommunityResponseDTO;
import com.himedia.luckydokiapi.domain.community.dto.CommunitySearchDTO;
import com.himedia.luckydokiapi.domain.community.entity.Community;
import com.himedia.luckydokiapi.domain.community.repository.CommunityRepository;
import com.himedia.luckydokiapi.domain.member.entity.Member;
import com.himedia.luckydokiapi.domain.member.service.MemberService;
import com.himedia.luckydokiapi.domain.product.service.ProductService;
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
    private final ProductService productService;

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


    public CommunityResponseDTO postCommunity(String email, CommunityRequestDTO request) {
        log.info("🔹 postCommunity 요청 email: {}, request: {}", email, request);

        if (email == null) {
            log.error("오류 발생: email이 null입니다!");
            throw new IllegalArgumentException("email은 필수 입력값입니다.");
        }

        Member member = this.getMember(email);
        if (member == null) {
            log.error("오류 발생: 사용자를 찾을 수 없음 (email: {})", email);
            throw new EntityNotFoundException("사용자를 찾을 수 없습니다.");
        }

        Community community = Community.builder()
                .member(member)
                .content(request.getContent())
                .imageList(request.getImageList())
                .build();
        communityRepository.save(community);


//        if (request.getProductIds() != null && !request.getProductIds().isEmpty()) {
//            List<Product> products = productService.findProductsByIds(request.getProductIds()); // 상품 조회
//            List<CommunityProduct> communityProducts = products.stream()
//                    .map(product -> CommunityProduct.from(community, product))
//                    .collect(Collectors.toList());
//            community.getCommunityProductList().addAll(communityProducts);
//        }

        log.info("게시글 저장 완료! ID: {}", community.getId());

        return toDTO(community);
    }



    public CommunityResponseDTO updateCommunity(Long communityId, String email, CommunityRequestDTO request) {

        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new EntityNotFoundException("해당 게시글이 존재하지 않습니다."));

        Member member = this.getMember(email);

        if (!community.getMember().getEmail().equals(member.getEmail())) {
            throw new IllegalArgumentException("본인의 게시글만 수정할 수 있습니다.");
        }

        community.setContent(request.getContent());
        community.setImageList(request.getImageList());

        log.info(" 게시글 수정 완료: communityId={}", communityId);
        return toDTO(community);
    }


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

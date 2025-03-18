package com.himedia.luckydokiapi.domain.community.service;

import com.himedia.luckydokiapi.config.RedisConfig;
import com.himedia.luckydokiapi.domain.community.dto.CommunityRequestDTO;
import com.himedia.luckydokiapi.domain.community.dto.CommunityResponseDTO;
import com.himedia.luckydokiapi.domain.community.dto.CommunitySearchDTO;
import com.himedia.luckydokiapi.domain.community.entity.Community;
import com.himedia.luckydokiapi.domain.community.entity.CommunityImage;
import com.himedia.luckydokiapi.domain.community.entity.CommunityProduct;
import com.himedia.luckydokiapi.domain.community.entity.CommunityTag;
import com.himedia.luckydokiapi.domain.community.repository.CommunityRepository;
import com.himedia.luckydokiapi.domain.community.repository.CommunityTagRepository;
import com.himedia.luckydokiapi.domain.member.entity.Member;
import com.himedia.luckydokiapi.domain.member.enums.MemberRole;
import com.himedia.luckydokiapi.domain.member.repository.MemberRepository;
import com.himedia.luckydokiapi.domain.member.service.MemberService;
import com.himedia.luckydokiapi.domain.product.entity.Product;
import com.himedia.luckydokiapi.domain.product.entity.Tag;
import com.himedia.luckydokiapi.domain.product.repository.ProductRepository;
import com.himedia.luckydokiapi.domain.product.repository.TagRepository;
import com.himedia.luckydokiapi.domain.search.service.IndexingService;
import com.himedia.luckydokiapi.dto.PageResponseDTO;
import com.himedia.luckydokiapi.util.file.CustomFileUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CommunityServiceImpl implements CommunityService {

    private final CommunityRepository communityRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final CustomFileUtil fileUtil;
    private final MemberService memberService;
    private final TagRepository tagRepository;
    private final CommunityTagRepository communityTagRepository;

    private final IndexingService indexingService;

    
    @Cacheable(key = "#communityId", value = RedisConfig.COMMUNITY_DETAIL, cacheManager = "redisCacheManager")
    @Transactional(readOnly = true)
    @Override
    public CommunityResponseDTO getCommunityById(Long communityId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new EntityNotFoundException("해당 게시글이 존재하지 않습니다."));
        return CommunityResponseDTO.toDto(community);
    }

    @Cacheable(key = "#request.toString() + '_' + #email", value = RedisConfig.COMMUNITY_LIST, cacheManager = "redisCacheManager")
    @Transactional(readOnly = true)
    @Override
    public List<CommunityResponseDTO> list(CommunitySearchDTO request, String email) {
        List<CommunityResponseDTO> dtoList = communityRepository.findByDTO(request).stream()
                .map(CommunityResponseDTO::toDto)
                .toList();

        return dtoList;
    }

    // TODO: 오류문제💥class java.util.LinkedHashMap cannot be cast to class com.himedia.luckydokiapi.dto.PageResponseDTO (java.util.LinkedHashMap is in module java.base of loader 'bootstrap'; com.himedia.luckydokiapi.dto.PageResponseDTO is in unnamed module of loader 'app')
//    @Cacheable(
//            key = "'page_' + #requestDTO.getPage() + '_size_' + #requestDTO.getSize() + '_email_' + #email",
//            value = RedisConfig.COMMUNITY_PAGE,
//            cacheManager = "redisCacheManager"
//    )
    @Transactional(readOnly = true)
    @Override
    public PageResponseDTO<CommunityResponseDTO> listPage(CommunitySearchDTO requestDTO, String email) {
        Page<Community> result = communityRepository.findListBy(requestDTO);
        return PageResponseDTO.<CommunityResponseDTO>withAll()
                .dtoList(result.stream().map(CommunityResponseDTO::toDto).collect(Collectors.toList()))
                .totalCount(result.getTotalElements())
                .pageRequestDTO(requestDTO)
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public List<CommunityResponseDTO> getCommunitiesByMemberEmail(String email) {
        return communityRepository.findByMemberEmail(email).stream()
                .map(CommunityResponseDTO::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Long postCommunity(String email, CommunityRequestDTO request) {
        Member member = memberService.getEntity(email);
        // 상품을 등록하는 경우에만 셀러인지 확인
        if (request.getProductIds() != null && !request.getProductIds().isEmpty()) {
            if (!member.getMemberRoleList().contains(MemberRole.SELLER)) {
                throw new IllegalArgumentException("셀러만 상품을 등록할 수 있습니다. email: " + email);
            }
        }

        // 파일 업로드 (누구나 가능)
        if (request.getFiles() == null || request.getFiles().isEmpty()) {
            throw new IllegalArgumentException("이미지 파일은 필수 입력 항목입니다.");
        }

        try {
            List<MultipartFile> files = request.getFiles();
            List<String> uploadS3FilesNames = fileUtil.uploadS3Files(files);
            log.info("uploadS3FilesNames: {}", uploadS3FilesNames);
            request.setUploadFileNames(uploadS3FilesNames);
        } catch (Exception e) {
            throw new IllegalArgumentException("이미지 파일 업로드 중 오류가 발생했습니다.");
        }

        Community newCommunity = this.dtoToEntity(request, member);

        // 사용자가 등록한 상품만 추가할 수 있도록 검증
        if (request.getProductIds() != null && !request.getProductIds().isEmpty()) {
            List<Product> sellerProducts = productRepository.findProductByShopMemberEmail(email);

            List<Long> sellerProductIds = sellerProducts.stream().map(Product::getId).toList();
            log.info("sellerProductIds: {}", sellerProductIds);

            // 셀러가 등록한 상품과 요청한 상품이 일치하는지 확인 -> 하나라도 다르면 예외발생
            List<Product> requestProducts = new ArrayList<>();
            request.getProductIds().forEach(productId -> {
                if (!sellerProductIds.contains(productId)) {
                    throw new IllegalArgumentException("현재 셀러가 등록한 상품이 아닙니다. productId: " + productId);
                }
                requestProducts.add(productRepository.findById(productId).orElseThrow());
            });
            // 가져온 상품 리스트로 CommunityProduct 생성
            requestProducts.forEach(product ->
                    newCommunity.addProduct(CommunityProduct.from(newCommunity, product)));
        }

        Community result = communityRepository.save(newCommunity);


        if (request.getTagStrList() != null) {
            request.getTagStrList().forEach(tagStr -> {
                String tagName = tagStr.trim();
                log.info("tagName: {}", tagName);
                Tag newTag = null;
                if (tagRepository.existsByName(tagName)) {
                    log.info("이미 존재하는 태그입니다");
                    newTag = tagRepository.findByName(tagName);
                } else {
                    log.info("새로운 태그 등록 newTag ={}", newTag);
                    newTag = tagRepository.save(Tag.from(tagStr));
                }
                communityTagRepository.save(CommunityTag.from(newTag, result));
            });
            //tag 가 null 일 경우
        } else {
            log.info("등록돤 태그가 없습니다");
            return result.getId();
        }

        log.info("newCommunity: {}", newCommunity);

        // elasticsearch indexing
        indexingService.indexCommunity(result.getId(), "CREATE");
        
        // 목록 캐시 삭제
        clearCommunityListCache();

        return result.getId();
    }

/*    @CacheEvict(key = "#communityId", value = RedisConfig.COMMUNITY_DETAIL, cacheManager = "redisCacheManager")
    @Override
    public CommunityResponseDTO updateCommunity(Long communityId, String email, CommunityRequestDTO request) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new EntityNotFoundException("해당 게시글이 존재하지 않습니다."));

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        if (!community.getMember().getEmail().equals(member.getEmail())) {
            throw new IllegalArgumentException("본인의 게시글만 수정할 수 있습니다.");
        }

        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("제목은 필수 입력 항목입니다.");
        }
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("내용은 필수 입력 항목입니다.");
        }

        // 기존 이미지 리스트 가져오기
        List<String> oldFileNames = community.getImageList().stream()
                .map(CommunityImage::getImageName)
                .toList();

        // 새로 업로드할 파일 S3 저장
        List<String> newUploadFileNames = new ArrayList<>();
        if (request.getFiles() != null && !request.getFiles().isEmpty()) {
            newUploadFileNames = fileUtil.uploadS3Files(request.getFiles());
        }

        // 유지되는 파일 가져오기
        List<String> uploadedFileNames = request.getUploadFileNames();

        // 기존 파일 삭제 처리
        if (oldFileNames != null && !oldFileNames.isEmpty()) {
            List<String> removeFiles = oldFileNames.stream()
                    .filter(fileName -> !uploadedFileNames.contains(fileName))
                    .toList();

            fileUtil.deleteS3Files(removeFiles);
        }

        //  유지된 파일 + 새로 업로드된 파일 최종 적용
        if (!newUploadFileNames.isEmpty()) {
            uploadedFileNames.addAll(newUploadFileNames);
        }

        // 기존 이미지 리스트 삭제 후 새 리스트 적용
        community.getImageList().clear();
        if (!uploadedFileNames.isEmpty()) {
            int order = 1;
            for (String fileName : uploadedFileNames) {
                CommunityImage communityImage = CommunityImage.builder()
                        .community(community)
                        .imageName(fileName)
                        .ord(order++)
                        .build();
                community.getImageList().add(communityImage);
            }
        }

        community.setTitle(request.getTitle());
        community.setContent(request.getContent());
        Community result = communityRepository.save(community);

        // 수정이 완료된 후 목록 캐시도 삭제
        clearCommunityListCache();
        
        return CommunityResponseDTO.toDto(result);
    }*/

    @CacheEvict(key = "#communityId", value = RedisConfig.COMMUNITY_DETAIL, cacheManager = "redisCacheManager")
    @Override
    public void deleteCommunity(Long communityId, String email) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new EntityNotFoundException("해당 게시글이 존재하지 않습니다."));

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        if (!community.getMember().getEmail().equals(member.getEmail())) {
            throw new IllegalArgumentException("본인의 게시글만 삭제할 수 있습니다.");
        }

        // S3 파일 삭제
        List<String> deleteImages = community.getImageList().stream()
                .map(CommunityImage::getImageName)
                .collect(Collectors.toList());
        fileUtil.deleteS3Files(deleteImages);
        communityTagRepository.deleteByCommunity(community);
        communityRepository.delete(community);

        // elasticsearch indexing
        indexingService.indexCommunity(communityId, "DELETE");
        
        // 목록 캐시도 삭제
        clearCommunityListCache();
    }

    // 커뮤니티 목록 캐시 전체 삭제
    @CacheEvict(value = {RedisConfig.COMMUNITY_LIST, RedisConfig.COMMUNITY_PAGE}, allEntries = true, cacheManager = "redisCacheManager")
    public void clearCommunityListCache() {
        log.info("커뮤니티 목록 캐시 전체 삭제");
    }

}



package com.himedia.luckydokiapi.domain.community.service;

import com.himedia.luckydokiapi.domain.community.dto.CommunityRequestDTO;
import com.himedia.luckydokiapi.domain.community.dto.CommunityResponseDTO;
import com.himedia.luckydokiapi.domain.community.dto.CommunitySearchDTO;
import com.himedia.luckydokiapi.domain.community.entity.Community;
import com.himedia.luckydokiapi.domain.community.entity.CommunityImage;
import com.himedia.luckydokiapi.domain.community.entity.CommunityProduct;
import com.himedia.luckydokiapi.domain.community.repository.CommunityRepository;
import com.himedia.luckydokiapi.domain.member.entity.Member;
import com.himedia.luckydokiapi.domain.member.repository.MemberRepository;
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
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final CustomFileUtil customFileUtil;

    @Transactional(readOnly = true)
    @Override
    public CommunityResponseDTO getCommunityById(Long communityId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new EntityNotFoundException("해당 게시글이 존재하지 않습니다."));
        return toDTO(community);
    }


    @Transactional(readOnly = true)
    @Override
    public List<CommunityResponseDTO> getAllCommunities(CommunitySearchDTO request) {
        return communityRepository.findByDTO(request).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<CommunityResponseDTO> getCommunitiesByMemberEmail(String email) {
        return communityRepository.findByMemberEmail(email).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CommunityResponseDTO postCommunity(String email, CommunityRequestDTO request) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        Community community = Community.builder()
                .member(member)
                .title(request.getTitle())
                .content(request.getContent())
                .build();

        // 파일 업로드는 서비스에서 처리 (Product와 동일)
        List<String> uploadFileNames = customFileUtil.uploadS3Files(request.getFiles());
        request.setUploadFileNames(uploadFileNames);

        if (request.getProductIds() != null && !request.getProductIds().isEmpty()) {
            request.getProductIds().forEach(productId -> {
                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new RuntimeException("존재하지 않는 상품입니다."));
                community.getCommunityProductList().add(CommunityProduct.from(community, product));
            });
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
//        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
//            throw new IllegalArgumentException("제목은 필수 입력 항목입니다.");
//        }
//        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
//            throw new IllegalArgumentException("내용은 필수 입력 항목입니다.");
//        }
//
//        // 기존 이미지 리스트 가져오기
//        List<String> oldFileNames = community.getImageList().stream()
//                .map(CommunityImage::getImageName)
//                .toList();
//
//        // 새로 업로드할 파일 S3 저장
//        List<String> newUploadFileNames = new ArrayList<>();
//        if (request.getFiles() != null && !request.getFiles().isEmpty()) {
//            newUploadFileNames = fileUtil.uploadS3Files(request.getFiles());
//        }
//
//        // 유지되는 파일 가져오기
//        List<String> uploadedFileNames = request.getUploadFileNames();
//
//        // 기존 파일 삭제 처리
//        if (oldFileNames != null && !oldFileNames.isEmpty()) {
//            List<String> removeFiles = oldFileNames.stream()
//                    .filter(fileName -> !uploadedFileNames.contains(fileName))
//                    .toList();
//
//            fileUtil.deleteS3Files(removeFiles);
//        }
//
//        //  유지된 파일 + 새로 업로드된 파일 최종 적용
//        if (!newUploadFileNames.isEmpty()) {
//            uploadedFileNames.addAll(newUploadFileNames);
//        }
//
//        // 기존 이미지 리스트 삭제 후 새 리스트 적용
//        community.getImageList().clear();
//        if (!uploadedFileNames.isEmpty()) {
//            int order = 1;
//            for (String fileName : uploadedFileNames) {
//                CommunityImage communityImage = CommunityImage.builder()
//                        .community(community)
//                        .imageName(fileName)
//                        .ord(order++)
//                        .build();
//                community.getImageList().add(communityImage);
//            }
//        }
//
//        community.setTitle(request.getTitle());
//        community.setContent(request.getContent());
//        communityRepository.save(community);
//        return toDTO(community);
//    }

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
        customFileUtil.deleteS3Files(deleteImages);

        communityRepository.delete(community);
    }



}



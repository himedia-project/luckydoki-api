package com.himedia.luckydokiapi.domain.shop.service;

import com.himedia.luckydokiapi.domain.member.dto.SellerResponseDTO;
import com.himedia.luckydokiapi.domain.member.entity.Member;
import com.himedia.luckydokiapi.domain.member.entity.SellerApplication;
import com.himedia.luckydokiapi.domain.member.enums.MemberRole;
import com.himedia.luckydokiapi.domain.member.enums.ShopApproved;
import com.himedia.luckydokiapi.domain.member.repository.MemberRepository;
import com.himedia.luckydokiapi.domain.member.service.MemberService;
import com.himedia.luckydokiapi.domain.shop.dto.SellerSearchDTO;
import com.himedia.luckydokiapi.domain.shop.dto.ShopResponseDTO;
import com.himedia.luckydokiapi.domain.shop.dto.ShopSearchDTO;
import com.himedia.luckydokiapi.domain.shop.entity.Shop;
import com.himedia.luckydokiapi.domain.member.service.SellerApplicationRepository;
import com.himedia.luckydokiapi.domain.shop.repository.ShopRepository;
import com.himedia.luckydokiapi.dto.PageResponseDTO;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AdminShopServiceImpl implements AdminShopService {

    private final ShopRepository shopRepository;
    private final SellerApplicationRepository sellerApplicationRepository;
    private final MemberService memberService;
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    @Override
    public PageResponseDTO<ShopResponseDTO> list(ShopSearchDTO request) {

        Page<Shop> result = shopRepository.findListBy(request);
        return PageResponseDTO.<ShopResponseDTO>withAll()
                .dtoList(result.stream().map(this::convertToDTO).toList())
                .totalCount(result.getTotalElements())
                .pageRequestDTO(request)
                .build();
    }

    /**
     * 셀러 신청 승인
     */
    @Override
    public Long approveSeller(Long applicationId) {
        SellerApplication application = getApplication(applicationId);

        Member member = getMember(application.getEmail());

        // 맴버 권한 유저 -> seller
        member.changeRole(MemberRole.SELLER);
        memberRepository.save(member);
        // 셀러 폼 승인 처리
        application.approve();

        // ✅ 승인된 셀러를 Shop에 자동 등록
        // 이메일을 기준으로 샵이 이미 있으면 예외처리
        shopRepository.findByMemberEmail(application.getEmail())
                .ifPresent(shop -> {
                    throw new IllegalArgumentException("이미 shop이 있는 회원입니다. shopId" + shop.getId());
                });

        // 샵이 없으므로 샵 save
        Shop shop = Shop.builder()
                .image(application.getShopImage())
                .introduction(application.getIntroduction())
                .member(member)
                .build();
        Shop saved = shopRepository.save(shop);

        return saved.getId();
    }

    /**
     * 승인되지 않은 셀러 신청 목록 조회
     */

    @Transactional(readOnly = true)
    @Override
    public PageResponseDTO<SellerResponseDTO> getPendingApplications(SellerSearchDTO requestDTO) {

        Pageable pageable = PageRequest.of(
                requestDTO.getPage() - 1,  //페이지 시작 번호가 0부터 시작하므로
                requestDTO.getSize(),
                "asc".equals(requestDTO.getSort()) ?  // 정렬 조건
                        Sort.by("id").ascending() : Sort.by("id").descending()
        );

        Page<SellerApplication> pageList = sellerApplicationRepository.findListBy(requestDTO, pageable);

        return PageResponseDTO.<SellerResponseDTO>withAll()
                .dtoList(pageList.stream().map(this::convertToDTO).toList())
                .totalCount(pageList.getTotalElements())
                .pageRequestDTO(requestDTO)
                .build();
    }



    /**
     * 승인된 셀러 신청 목록 조회
     */
    @Transactional(readOnly = true)
    public List<SellerResponseDTO> getApprovedApplications() {
        return sellerApplicationRepository.findByIsApproved(ShopApproved.Y)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 셀러 리스트 조회
     *
     */
    @Transactional(readOnly = true)
    @Override
    public List<ShopResponseDTO> optionList() {
        return shopRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }


    /**
     * application 찾기
     * @param applicationId 어플리메이션 id
     * @return 어플리케이션
     */
    public SellerApplication getApplication(Long applicationId) {
        return sellerApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new EntityNotFoundException("해당 신청이 존재하지 않습니다. id: " + applicationId));
    }

    /**
     * 이메일로 맴버 찾기
     * @param email 이메일
     * @return Member
     */
    private Member getMember(String email) {
        return memberService.getEntity(email);
    }
}

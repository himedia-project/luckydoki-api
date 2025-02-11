package com.himedia.luckydokiapi.domain.shop.service;

import com.himedia.luckydokiapi.domain.member.dto.SellerResponseDTO;
import com.himedia.luckydokiapi.domain.member.entity.Member;
import com.himedia.luckydokiapi.domain.member.entity.SellerApplication;
import com.himedia.luckydokiapi.domain.member.enums.MemberRole;
import com.himedia.luckydokiapi.domain.member.repository.MemberRepository;
import com.himedia.luckydokiapi.domain.member.repository.SellerApplicationRepository;
import com.himedia.luckydokiapi.domain.member.service.MemberService;
import com.himedia.luckydokiapi.domain.shop.entity.Shop;
import com.himedia.luckydokiapi.domain.shop.repository.ShopRepository;
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
public class AdminShopServiceImpl implements AdminShopService {

    private final ShopRepository shopRepository;
    private final SellerApplicationRepository sellerApplicationRepository;
    private final MemberService memberService;
    private final MemberRepository memberRepository;

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
    public List<SellerResponseDTO> getPendingApplications() {
        return sellerApplicationRepository.findByIsApproved(false)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    /**
     * 승인된 셀러 신청 목록 조회
     */
    @Transactional(readOnly = true)
    public List<SellerResponseDTO> getApprovedApplications() {
        return sellerApplicationRepository.findByIsApproved(true)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
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

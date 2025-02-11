package com.himedia.luckydokiapi.domain.member.service;


import com.himedia.luckydokiapi.domain.member.dto.MemberRequestDTO;
import com.himedia.luckydokiapi.domain.member.dto.MemberResDTO;
import com.himedia.luckydokiapi.domain.member.dto.SellerRequestDTO;
import com.himedia.luckydokiapi.domain.member.dto.SellerResponseDTO;
import com.himedia.luckydokiapi.domain.member.entity.Member;
import com.himedia.luckydokiapi.domain.member.entity.SellerApplication;
import com.himedia.luckydokiapi.domain.member.enums.MemberRole;
import com.himedia.luckydokiapi.domain.member.repository.MemberRepository;
import com.himedia.luckydokiapi.domain.member.repository.SellerApplicationRepository;
import com.himedia.luckydokiapi.domain.shop.entity.Shop;
import com.himedia.luckydokiapi.domain.shop.repository.ShopRepository;
import com.himedia.luckydokiapi.dto.PageRequestDTO;
import com.himedia.luckydokiapi.dto.PageResponseDTO;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@Service
public class AdminMemberServiceImpl implements AdminMemberService {

    private final MemberRepository memberRepository;
    private final SellerApplicationRepository sellerApplicationRepository;
    private final ShopRepository shopRepository;

    @Transactional(readOnly = true)
    @Override
    public PageResponseDTO<MemberResDTO> getList(MemberRequestDTO requestDTO) {

        Page<Member> result = memberRepository.findAllBy(requestDTO);

        return PageResponseDTO.<MemberResDTO>withAll()
                .dtoList(result.stream().map(this::entityToDTO).toList())
                .totalCount(result.getTotalElements())
                .pageRequestDTO(requestDTO)
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public MemberResDTO getOne(String email) {
        return entityToDTO(getMember(email));
    }


    /**
     * member 찾기
     * @param email 이메일
     * @return Member
     */
    private Member getMember(String email) {
        return memberRepository.findById(email)
                .orElseThrow(() -> new EntityNotFoundException("해당 회원이 존재하지 않습니다. email: " + email));
    }


    /**
     *  승인되지 않은 셀러 신청 목록 조회
     */

    @Transactional(readOnly = true)
    public List<SellerResponseDTO> getPendingApplications() {
        return sellerApplicationRepository.findByIsApproved(false)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    /**
     *  승인된 셀러 신청 목록 조회
     */
    @Transactional(readOnly = true)
    public List<SellerResponseDTO> getApprovedApplications() {
        return sellerApplicationRepository.findByIsApproved(true)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     *  셀러 신청 승인
     */
    public SellerResponseDTO approveSeller(Long applicationId) {
        SellerApplication application = sellerApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new EntityNotFoundException("해당 신청이 존재하지 않습니다. id: " + applicationId));

        Member member = memberRepository.findByEmail(application.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("해당 회원이 존재하지 않습니다. email: " + application.getEmail()));

        member.changeRole(MemberRole.SELLER);
        memberRepository.save(member);

        application.approve();
        sellerApplicationRepository.save(application);

        // ✅ 승인된 셀러를 Shop에 자동 등록
        if (shopRepository.findByMemberEmail(member.getEmail()).isEmpty()) {
            Shop shop = Shop.builder()
                    .member(member)
                    .shopLikes(new ArrayList<>())
                    .productList(new ArrayList<>())
                    .build();
            shopRepository.save(shop);
        }
        return convertToDTO(application);
    }


    private SellerResponseDTO convertToDTO(SellerApplication application) {
        return SellerResponseDTO.builder()
                .id(application.getId())
                .email(application.getEmail())
                .nickName(application.getNickName())
                .profileImage(application.getProfileImage())
                .introduction(application.getIntroduction())
                .isApproved(application.isApproved())
                .statusDescription(application.isApproved() ? "승인 완료" : "승인 대기")
                .build();
    }
}

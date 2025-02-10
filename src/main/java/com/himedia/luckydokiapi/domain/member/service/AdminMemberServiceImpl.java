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
import com.himedia.luckydokiapi.dto.PageRequestDTO;
import com.himedia.luckydokiapi.dto.PageResponseDTO;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@Service
public class AdminMemberServiceImpl implements AdminMemberService {

    private final MemberRepository memberRepository;
    private final SellerApplicationRepository sellerApplicationRepository;

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

    @Override
    public SellerResponseDTO applyForSeller(SellerRequestDTO requestDTO) {
        // 1. 회원 정보 조회 (이메일 기준)
        Member member = memberRepository.findById(requestDTO.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("해당 회원이 존재하지 않습니다. email: " + requestDTO.getEmail()));

        // 2. 이미 신청했는지 확인
        boolean alreadyExists = sellerApplicationRepository.findByEmail(requestDTO.getEmail()).isPresent();
        if (alreadyExists) {
            throw new IllegalStateException("이미 셀러 신청을 완료한 회원입니다.");
        }

        // 3. 신청 정보 생성 및 저장 (isApproved = false)
        SellerApplication application = SellerApplication.builder()
                .email(member.getEmail())
                .nickName(member.getNickName())
                .isApproved(false)
                .build();

        sellerApplicationRepository.save(application);
        return convertToDTO(application);

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
        // 1. 신청 내역 조회
        SellerApplication application = sellerApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new EntityNotFoundException("해당 신청이 존재하지 않습니다. id: " + applicationId));

        // 2. 회원 정보 조회
        Member member = memberRepository.findByEmail(application.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("해당 회원이 존재하지 않습니다. email: " + application.getEmail()));

        // 3. SELLER 역할 부여
        if (!member.getMemberRoleList().contains(MemberRole.SELLER)) {
            member.addRole(MemberRole.SELLER);
            memberRepository.save(member);
        }

        // 4. 신청 상태를 승인 완료로 변경
        application.approve();
        sellerApplicationRepository.save(application);

        // 5. 승인된 정보를 DTO로 변환하여 반환
        return SellerResponseDTO.builder()
                .id(application.getId())
                .email(application.getEmail())
                .nickName(application.getNickName())
                .isApproved(application.isApproved())
                .statusDescription(application.isApproved() ? "승인 완료" : "승인 대기")
                .build();
    }



    private SellerResponseDTO convertToDTO(SellerApplication application) {
        return SellerResponseDTO.builder()
                .id(application.getId())
                .email(application.getEmail())
                .nickName(application.getNickName())
                .isApproved(application.isApproved())
                .statusDescription(application.isApproved() ? "승인 완료" : "승인 대기")
                .build();
    }
}

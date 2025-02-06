package com.himedia.luckydokiapi.domain.member.service;


import com.himedia.luckydokiapi.domain.member.dto.MemberRequestDTO;
import com.himedia.luckydokiapi.domain.member.dto.MemberResDTO;
import com.himedia.luckydokiapi.domain.member.dto.SellerRequestDTO;
import com.himedia.luckydokiapi.domain.member.dto.SellerResponseDTO;
import com.himedia.luckydokiapi.domain.member.entity.Member;
import com.himedia.luckydokiapi.dto.PageResponseDTO;

import java.util.List;

public interface AdminMemberService {

    SellerResponseDTO approveSeller(Long applicationId);
    SellerResponseDTO applyForSeller(SellerRequestDTO requestDTO);
    List<SellerResponseDTO> getPendingApplications();
    List<SellerResponseDTO> getApprovedApplications();

    PageResponseDTO<MemberResDTO> getList(MemberRequestDTO requestDTO);

    MemberResDTO getOne(String email);

    /**
     * Member Entity -> MemberResDTO
     * @param member Member Entity
     * @return MemberResDTO
     */
    default MemberResDTO entityToDTO(Member member){

        return MemberResDTO.builder()
                .email(member.getEmail())
                .nickName(member.getNickName())
                .phone(member.getPhone())
                .roles(member.getMemberRoleList())
                .createdAt(member.getCreatedAt())
                .modifiedAt(member.getModifiedAt())
                .build();
    }
}

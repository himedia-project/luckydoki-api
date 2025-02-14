package com.himedia.luckydokiapi.domain.member.service;


import com.himedia.luckydokiapi.domain.member.dto.MemberRequestDTO;
import com.himedia.luckydokiapi.domain.member.dto.MemberResDTO;
import com.himedia.luckydokiapi.domain.member.entity.Member;
import com.himedia.luckydokiapi.dto.PageResponseDTO;

public interface AdminMemberService {

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
                .active(member.getActive())
                .pushActive(member.getPushActive())
                .createdAt(member.getCreatedAt())
                .modifiedAt(member.getModifiedAt())
                .build();
    }
}

package com.himedia.luckydokiapi.domain.member.service;

import com.himedia.luckydokiapi.domain.member.dto.*;
import com.himedia.luckydokiapi.domain.member.entity.Member;
import com.himedia.luckydokiapi.domain.shop.entity.Shop;
import com.himedia.luckydokiapi.security.MemberDTO;
import jakarta.validation.Valid;

import java.util.Map;

public interface MemberService {
    Long upgradeToSeller(String email, SellerRequestDTO requestDTO);

    MemberDetailDTO getMyInfo(String email);

    MemberDetailDTO updateMyInfo(String email, UpdateMemberDTO request);

    Member getEntity(String email);

    Map<String, Object> login(String email, String password);

    LoginResponseDTO loginToDto(String email, String password);

    void join(@Valid JoinRequestDTO joinRequestDTO);

    /**
     * 회원 임시 비밀번호 발급
     *
     * @return 임시 비밀번호
     */
    String makeTempPassword();

    /**
     * 소셜 로그인 시 클레임 정보 반환
     *
     * @param memberDTO 회원정보 DTO
     * @return 클레임 정보
     */
    Map<String, Object> getSocialClaims(MemberDTO memberDTO);

    /**
     * 회원정보 Entity -> DTO 변환
     *
     * @param member 회원정보
     * @return 회원정보 DTO
     */
    default MemberDTO entityToDTO(Member member) {

        return new MemberDTO(
                member.getEmail(),
                member.getPassword(),
                member.getNickName(),
                member.getPhone(),
                member.getMemberRoleList().stream()
                        .map(Enum::name).toList(),
                member.getActive()
        );
    }

    /**
     * client 에 보낼 member 정보 MemberDTO 랑 다릅니다
     */
    default MemberDetailDTO entityToMemberDetailDTO(Member member, Shop shop) {
        String memberRoleName = member.getMemberRoleList().get(0).name();
        return MemberDetailDTO.builder()
                .nickName(member.getNickName())
                .email(member.getEmail())
                .roleName(memberRoleName)
                .phone(member.getPhone())
                .profileImage(member.getProfileImage())
                .shopId(shop != null ? shop.getId() : null)
                .sellerRequested(member.getSellerRequested())       // 셀러 신청 여부 확인
                .activeCouponCount(member.getActiveCouponCount())
                .build();
    }

    void deleteMember(String email);


    void updateFCMToken(String targetEmail, String fcmToken);

    Boolean checkEmail(String email);
}


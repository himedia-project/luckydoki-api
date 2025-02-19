package com.himedia.luckydokiapi.domain.member.service;

import com.himedia.luckydokiapi.domain.member.dto.JoinRequestDTO;
import com.himedia.luckydokiapi.domain.member.dto.MemberDetailDTO;
import com.himedia.luckydokiapi.domain.member.dto.SellerRequestDTO;
import com.himedia.luckydokiapi.domain.member.dto.UpdateMemberDTO;
import com.himedia.luckydokiapi.domain.member.entity.Member;
import com.himedia.luckydokiapi.domain.shop.entity.Shop;
import com.himedia.luckydokiapi.security.MemberDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.util.Map;

import static com.himedia.luckydokiapi.domain.member.enums.MemberRole.SELLER;

public interface MemberService {
    Long upgradeToSeller(String email, SellerRequestDTO requestDTO);

    MemberDetailDTO getMyInfo(String email);

    MemberDetailDTO updateMyInfo(String email, UpdateMemberDTO request);

    Member getEntity(String email);

    Map<String, Object> login(@NotBlank(message = "이메일을 입력해주세요") String email, @NotBlank(message = "패스워드를  입력해주세요") String password);

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
    default MemberDetailDTO entityToMemberDetailDTO(Member member, Shop shop, Boolean sellerRequested) {
        String memberRoleName = member.getMemberRoleList().get(0).name();
        return MemberDetailDTO.builder()
                .nickName(member.getNickName())
                .email(member.getEmail())
                .roleName(memberRoleName)
                .phone(member.getPhone())
                .shopId(shop != null ? shop.getId() : null)
                .sellerRequested(sellerRequested)
                .activeCouponCount(member.getActiveCouponCount())
                .build();
    }

    void deleteMember(String email);


}


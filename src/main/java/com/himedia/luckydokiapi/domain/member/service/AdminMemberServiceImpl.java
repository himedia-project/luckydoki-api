package com.himedia.luckydokiapi.domain.member.service;


import com.himedia.luckydokiapi.domain.member.dto.MemberRequestDTO;
import com.himedia.luckydokiapi.domain.member.dto.MemberResDTO;
import com.himedia.luckydokiapi.domain.member.entity.Member;
import com.himedia.luckydokiapi.domain.member.repository.MemberRepository;
import com.himedia.luckydokiapi.dto.PageResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class AdminMemberServiceImpl implements AdminMemberService {

    private final MemberRepository memberRepository;
    private final MemberService memberService;

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
     *
     * @param email 이메일
     * @return Member
     */
    private Member getMember(String email) {
        return memberService.getEntity(email);
    }

}

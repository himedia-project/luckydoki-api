package com.himedia.luckydokiapi.domain.member.repository.querydsl;


import com.himedia.luckydokiapi.domain.member.dto.MemberRequestDTO;
import com.himedia.luckydokiapi.domain.member.entity.Member;
import org.springframework.data.domain.Page;

public interface MemberRepositoryCustom {

    Page<Member> findAllBy(MemberRequestDTO requestDTO);
}

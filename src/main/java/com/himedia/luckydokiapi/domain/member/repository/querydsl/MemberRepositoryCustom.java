package com.himedia.luckydokiapi.domain.member.repository.querydsl;


import com.himedia.luckydokiapi.domain.member.dto.MemberRequestDTO;
import com.himedia.luckydokiapi.domain.member.entity.Member;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public interface MemberRepositoryCustom {

    Page<Member> findAllBy(MemberRequestDTO requestDTO);

    List<Member> findTop5Sellers();

    List<Member> findTop5GoodConsumers();

    Long countBySellerApprovedIsFalse();

    Long countNewSellersInLastMonth(LocalDateTime monthAgo);
}

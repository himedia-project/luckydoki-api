package com.himedia.luckydokiapi.domain.member.service;

import com.himedia.luckydokiapi.domain.member.entity.Member;
import com.himedia.luckydokiapi.domain.member.enums.MemberRole;
import com.himedia.luckydokiapi.domain.member.repository.MemberRepository;
import com.himedia.luckydokiapi.exception.NotSellerAccessException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SellerService {
    private final MemberRepository memberRepository;

    public void checkedSeller(String email) {
        log.info("Checking seller: {}", email);
        Member seller = memberRepository.getWithRoles(email).orElseThrow(()->new EntityNotFoundException("존재하지 않는 회웝입니다"));
        if(!seller.getMemberRoleList().contains(MemberRole.SELLER)){
            throw new NotSellerAccessException("seller 권한이 없습니다");
        }
    }
}

package com.himedia.luckydokiapi.domain.member.repository.querydsl;


import com.himedia.luckydokiapi.domain.member.dto.MemberRequestDTO;
import com.himedia.luckydokiapi.domain.member.entity.Member;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.himedia.luckydokiapi.domain.member.entity.QMember.member;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Member> findAllBy(MemberRequestDTO requestDTO) {
        Pageable pageable = PageRequest.of(
                requestDTO.getPage() - 1,  // 페이지 시작 번호가 0부터 시작하므로
                requestDTO.getSize(),
                "asc".equals(requestDTO.getSort()) ?  // 정렬 조건
                        Sort.by("id").ascending() : Sort.by("id").descending()
        );

        // pageable의 sort 정보를 적용
//        OrderSpecifier[] orderSpecifiers = createOrderSpecifier(pageable.getSort());

        List<Member> list = queryFactory
                .select(member)
                .from(member)
                .leftJoin(member.memberRoleList).fetchJoin()
                .where(
                        containsKeyword(requestDTO.getSearchKeyword())
                )
                .orderBy(member.createdAt.desc())  // pk 가 email 이므로 createdAt 으로 대체
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // pageable의 sort 정보를 querydsl에 적용

        JPAQuery<Member> countQuery = queryFactory
                .selectFrom(member)
                .leftJoin(member.memberRoleList).fetchJoin()
                .where(
                        containsKeyword(requestDTO.getSearchKeyword())
                );

        return PageableExecutionUtils.getPage(list, pageable, countQuery::fetchCount);
    }


    /**
     * Sort 정보를 OrderSpecifier 배열로 변환
     * @param sort Sort 정보
     * @return OrderSpecifier 배열
     */
    private OrderSpecifier [] createOrderSpecifier(Sort sort) {
        return sort.stream()
                .map(order -> new OrderSpecifier(
                        order.isAscending() ? Order.ASC : Order.DESC,
                        new PathBuilder<>(Member.class, "member").get(order.getProperty())
                ))
                .toArray(OrderSpecifier[]::new);
    }

    private BooleanExpression containsKeyword(String keyword) {
        if(keyword == null || keyword.isBlank()) {
            return null;
        }
        return member.email.contains(keyword)
                .or(member.nickName.contains(keyword));
    }

}

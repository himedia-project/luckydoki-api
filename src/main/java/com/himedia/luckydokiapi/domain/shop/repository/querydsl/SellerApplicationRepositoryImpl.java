package com.himedia.luckydokiapi.domain.shop.repository.querydsl;

import com.himedia.luckydokiapi.domain.member.entity.SellerApplication;
import com.himedia.luckydokiapi.domain.member.enums.ShopApproved;
import com.himedia.luckydokiapi.domain.shop.dto.SellerSearchDTO;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.himedia.luckydokiapi.domain.member.entity.QSellerApplication.sellerApplication;

@Slf4j
@Repository
@RequiredArgsConstructor
public class SellerApplicationRepositoryImpl implements SellerApplicationRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<SellerApplication> findListBy(SellerSearchDTO requestDTO, Pageable pageable) {

        List<SellerApplication> list = queryFactory
                .selectFrom(sellerApplication)
                .where(
                        containsSearchKeyword(requestDTO.getSearchKeyword())
                )
                .orderBy(sellerApplication.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<SellerApplication> countQuery = queryFactory
                .selectFrom(sellerApplication)
                .where(
                        containsSearchKeyword(requestDTO.getSearchKeyword())
                );

        return PageableExecutionUtils.getPage(list, pageable, countQuery::fetchCount);
    }


    private BooleanExpression containsSearchKeyword(String searchKeyword) {
        if (searchKeyword == null) {
            return null;  // 빠져나가는
        }
        return sellerApplication.email.contains(searchKeyword)
                .or(sellerApplication.introduction.contains(searchKeyword));

    }
}

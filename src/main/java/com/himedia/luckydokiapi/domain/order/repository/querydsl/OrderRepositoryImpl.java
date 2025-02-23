package com.himedia.luckydokiapi.domain.order.repository.querydsl;

import com.himedia.luckydokiapi.domain.order.controllor.AdminOrderController;
import com.himedia.luckydokiapi.domain.order.entity.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
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
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

import static com.himedia.luckydokiapi.domain.order.entity.QOrder.*;
import static com.himedia.luckydokiapi.domain.order.entity.QOrderItem.orderItem;

@Slf4j
@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepositoryCustom {

    private final JPAQueryFactory queryFactory;


    @Override
    public Page<Order> findListBy(AdminOrderController.OrderHisRequestDTO requestDTO) {

        Pageable pageable = PageRequest.of(
                requestDTO.getPage() - 1,  //페이지 시작 번호가 0부터 시작하므로
                requestDTO.getSize(),
                "asc".equals(requestDTO.getSort()) ?  // 정렬 조건
                        Sort.by("id").ascending() : Sort.by("id").descending()
        );

        List<Order> list = queryFactory
                .selectFrom(order)
                .leftJoin(order.orderItems, orderItem)
                .where(
                        containsSearchKeyword(requestDTO.getSearchKeyword()),
                        getEqOrderDateOfYear(requestDTO.getYear())
                )
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(getOrderSpecifier(pageable.getSort()))
                .fetch();

        JPAQuery<Order> countQuery = queryFactory
                .selectFrom(order)
                .leftJoin(order.orderItems, orderItem)
                .where(
                        containsSearchKeyword(requestDTO.getSearchKeyword()),
                        getEqOrderDateOfYear(requestDTO.getYear())
                );

        return PageableExecutionUtils.getPage(list, pageable, countQuery::fetchCount);
    }

    @Override
    public Integer calculateMonthlyRevenue(LocalDateTime monthAgo, LocalDateTime now) {
        return queryFactory
                .select(order.totalPrice.sum())
                .from(order)
                .where(
                        order.orderDate.between(monthAgo, now)
                )
                .fetchOne();

    }


    /**
     * 검색 조건을 생성
     *
     * @param searchKeyword 검색어
     * @return 검색 조건
     */
    private BooleanExpression containsSearchKeyword(String searchKeyword) {

        if (searchKeyword == null || searchKeyword.isBlank()) {
            return null;
        }
        return order.code.contains(searchKeyword.trim())
                .or(order.member.email.contains(searchKeyword.trim()));
    }

    /**
     * 연도에 해당하는 주문일자와 일치하는지 검사
     *
     * @param year 연도
     * @return 연도에 해당하는 주문일자와 일치하는지 검사하는 조건
     */
    private BooleanExpression getEqOrderDateOfYear(Integer year) {
        if (year == null) {
            // 현재 시점에서 6개월 전을 검색
            return null;

        }
        return order.orderDate.year().eq(year);
    }

    /**
     * Sort 정보를 OrderSpecifier 배열로 변환
     *
     * @param sort Sort 정보
     * @return OrderSpecifier 배열
     */

    private OrderSpecifier<Long> getOrderSpecifier(Sort sort) {
        return sort.equals(Sort.by(Sort.Order.asc("id"))) ? order.id.asc() : order.id.desc();
    }
}

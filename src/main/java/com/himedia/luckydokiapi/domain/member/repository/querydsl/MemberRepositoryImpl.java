package com.himedia.luckydokiapi.domain.member.repository.querydsl;


import com.himedia.luckydokiapi.domain.member.dto.MemberRequestDTO;
import com.himedia.luckydokiapi.domain.member.entity.Member;
import com.himedia.luckydokiapi.domain.member.enums.MemberActive;
import com.himedia.luckydokiapi.domain.member.enums.MemberRole;
import com.himedia.luckydokiapi.domain.member.enums.ShopApproved;
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

import java.time.LocalDateTime;
import java.util.List;

import static com.himedia.luckydokiapi.domain.member.entity.QMember.member;
import static com.himedia.luckydokiapi.domain.member.entity.QSellerApplication.sellerApplication;
import static com.himedia.luckydokiapi.domain.order.entity.QOrder.order;
import static com.himedia.luckydokiapi.domain.order.entity.QOrderItem.orderItem;
import static com.himedia.luckydokiapi.domain.product.entity.QProduct.product;
import static com.himedia.luckydokiapi.domain.review.entity.QReview.review;
import static com.himedia.luckydokiapi.domain.shop.entity.QShop.shop;

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

    @Override
    public List<Member> findTop5Sellers() {
        return queryFactory
                .selectFrom(member)
                .leftJoin(member.shop, shop)
                .leftJoin(shop.productList, product)
                .leftJoin(product.orderItems, orderItem)
                .where(
                        member.shop.isNotNull(),
                        member.memberRoleList.contains(MemberRole.SELLER),
                        member.active.ne(MemberActive.N)
                )
                .groupBy(member)
                .orderBy(
                        orderItem.count().desc(),                    // 판매량(주문 상품 수)
                        orderItem.orderPrice.sum().desc(),           // 판매액(주문 상품 가격의 합)
                        member.createdAt.desc()                             // 같은 순위일 경우 최신 판매자 우선
                )
                .limit(5)
                .fetch();
    }

    @Override
    public List<Member> findTop5GoodConsumers() {
        return queryFactory
                .selectFrom(member)
                .leftJoin(member.orderList, order)
                .leftJoin(member.reviewList, review)
                .where(
                        member.active.ne(MemberActive.N),
                        review.content.length().goe(10)
                )
                .groupBy(member)
                .orderBy(
                        order.count().add(review.count()).desc()
                )
                .limit(5)
                .fetch();

    }

    @Override
    public Long countBySellerApprovedIsFalse() {
        return queryFactory
                .selectFrom(member)
                .leftJoin(member.sellerApplicationList, sellerApplication)
                .where(
                        member.active.ne(MemberActive.N),
                        sellerApplication.approved.eq(ShopApproved.N)
                )
                .fetchCount();
    }

    @Override
    public Long countNewSellersInLastMonth(LocalDateTime monthAgo) {
        return queryFactory
                .selectFrom(member)
                .leftJoin(member.sellerApplicationList, sellerApplication)
                .where(
                        sellerApplication.isNotNull(),
                        sellerApplication.approved.eq(ShopApproved.Y),
                        sellerApplication.createdAt.after(monthAgo)
                )
                .fetchCount();
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

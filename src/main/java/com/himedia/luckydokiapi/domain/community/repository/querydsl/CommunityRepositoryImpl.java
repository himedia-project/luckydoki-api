package com.himedia.luckydokiapi.domain.community.repository.querydsl;


import com.himedia.luckydokiapi.domain.community.dto.CommunitySearchDTO;
import com.himedia.luckydokiapi.domain.community.entity.Community;
import com.himedia.luckydokiapi.domain.product.entity.Product;
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

import static com.himedia.luckydokiapi.domain.community.entity.QComment.comment;
import static com.himedia.luckydokiapi.domain.community.entity.QCommunity.community;
import static com.himedia.luckydokiapi.domain.community.entity.QCommunityImage.communityImage;
import static com.himedia.luckydokiapi.domain.community.entity.QCommunityProduct.communityProduct;
import static com.himedia.luckydokiapi.domain.community.entity.QCommunityTag.communityTag;
import static com.himedia.luckydokiapi.domain.product.entity.QProduct.product;
import static com.himedia.luckydokiapi.domain.product.entity.QProductImage.productImage;
import static com.himedia.luckydokiapi.domain.product.entity.QProductTag.productTag;
import static com.himedia.luckydokiapi.domain.product.entity.QTag.tag;

@Repository
@RequiredArgsConstructor
@Slf4j
public class CommunityRepositoryImpl implements CommunityRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Community> findListBy(CommunitySearchDTO requestDTO) {

        Pageable pageable = PageRequest.of(
                requestDTO.getPage() - 1,  //페이지 시작 번호가 0부터 시작하므로
                requestDTO.getSize(),
                "asc".equals(requestDTO.getSort()) ?  // 정렬 조건
                        Sort.by("id").ascending() : Sort.by("id").descending()
        );

        // pageable의 sort 정보를 적용
        OrderSpecifier[] orderSpecifiers = createOrderSpecifier(pageable.getSort());

        List<Community> list = queryFactory
                .select(community)
                .from(community)
                .leftJoin(community.imageList, communityImage).on(communityImage.ord.eq(0))
                .leftJoin(community.communityTagList, communityTag)
                .leftJoin(communityTag.tag, tag)
                .leftJoin(community.communityProductList, communityProduct)
                .leftJoin(communityProduct.product, product)
                .where(
                        containsSearchKeyword(requestDTO.getSearchKeyword())
                )
                .groupBy(community) // 커뮤니티별로 그룹화 -> 중복되지 않게 하기 위함
                .orderBy(orderSpecifiers)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        log.info("findListBy list : {}", list);

        // pageable의 sort 정보를 querydsl에 적용

        JPAQuery<Community> countQuery = queryFactory
                .select(community)
                .from(community)
                .leftJoin(community.imageList, communityImage).on(communityImage.ord.eq(0))
                .leftJoin(community.communityTagList, communityTag)
                .leftJoin(communityTag.tag, tag)
                .leftJoin(community.communityProductList, communityProduct)
                .leftJoin(communityProduct.product, product)
                .where(
                        containsSearchKeyword(requestDTO.getSearchKeyword())
                )
                .groupBy(community) // 커뮤니티별로 그룹화 -> 중복되지 않게 하기 위함
                ;

        return PageableExecutionUtils.getPage(list, pageable, countQuery::fetchCount);
    }


    @Override
    public List<Community> findByDTO(CommunitySearchDTO requestDTO) {

        return queryFactory
                .selectFrom(community)
                .leftJoin(community.communityProductList, communityProduct).fetchJoin()
                .leftJoin(communityProduct.product, product).fetchJoin()
                .leftJoin(community.communityTagList, communityTag)
                .leftJoin(communityTag.tag, tag)
                .where(
                        containsSearchKeyword(requestDTO.getSearchKeyword())
                )
                .orderBy(community.id.desc())
                .distinct()
                .fetch();
    }


    @Override
    public List<Community> findTop10ByOrderByLikeCountAndCommentCountDesc() {
        return queryFactory
                .selectFrom(community)
                .leftJoin(community.imageList, communityImage).on(communityImage.ord.eq(0))
                .leftJoin(community.commentList, comment)
                // like 는 존재하지 않아서 제외
                // 댓글이 없으면 최신 등록순으로
                .groupBy(community)
                .orderBy(comment.count().desc(), community.id.desc())
                .limit(10)
                .fetch();

    }

    /**
     * Sort 정보를 OrderSpecifier 배열로 변환
     *
     * @param sort Sort 정보
     * @return OrderSpecifier 배열
     */
    private OrderSpecifier[] createOrderSpecifier(Sort sort) {
        return sort.stream()
                .map(order -> new OrderSpecifier(
                        order.isAscending() ? Order.ASC : Order.DESC,
                        new PathBuilder<>(Product.class, "community").get(order.getProperty())
                ))
                .toArray(OrderSpecifier[]::new);
    }


    private BooleanExpression containsSearchKeyword(String searchKeyword) {
        if (searchKeyword == null) {
            return null;
        }
        searchKeyword = searchKeyword.trim();
        return community.content.contains(searchKeyword)
                .or(product.name.contains(searchKeyword))
                .or(tag.name.contains(searchKeyword));

    }
}

package com.himedia.luckydokiapi.domain.community.repository.querydsl;


import com.himedia.luckydokiapi.domain.community.dto.CommunitySearchDTO;
import com.himedia.luckydokiapi.domain.community.entity.Community;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.himedia.luckydokiapi.domain.community.entity.QComment.comment;
import static com.himedia.luckydokiapi.domain.community.entity.QCommunity.community;
import static com.himedia.luckydokiapi.domain.community.entity.QCommunityImage.communityImage;
import static com.himedia.luckydokiapi.domain.community.entity.QCommunityProduct.communityProduct;
import static com.himedia.luckydokiapi.domain.product.entity.QProduct.product;

@Repository
@RequiredArgsConstructor
@Slf4j
public class CommunityRepositoryImpl implements CommunityRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Community> findByDTO(CommunitySearchDTO requestDTO) {

        return queryFactory
                .selectFrom(community)
                .leftJoin(community.communityProductList, communityProduct).fetchJoin()
                .leftJoin(communityProduct.product, product).fetchJoin()
//                .leftJoin(product.productTagList, productTag).fetchJoin()
//                .leftJoin(productTag.tag, tag).fetchJoin()
                .where(
                        containsKeyword(requestDTO.getSearchKeyword())
                )
                .orderBy(community.id.desc())
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

    private BooleanExpression containsKeyword(String searchKeyword) {
        if (searchKeyword == null) {
            return null;
        }
        return community.content.contains(searchKeyword)
                .or(product.name.contains(searchKeyword));
    }
}

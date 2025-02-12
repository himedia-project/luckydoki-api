package com.himedia.luckydokiapi.domain.community.repository.querydsl;


import com.himedia.luckydokiapi.domain.community.dto.CommunitySearchDTO;
import com.himedia.luckydokiapi.domain.community.entity.Community;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.himedia.luckydokiapi.domain.community.entity.QCommunity.community;
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

    private BooleanExpression containsKeyword(String searchKeyword) {
        if (searchKeyword == null) {
            return null;
        }
        return community.content.contains(searchKeyword)
                .or(product.name.contains(searchKeyword));
    }
}

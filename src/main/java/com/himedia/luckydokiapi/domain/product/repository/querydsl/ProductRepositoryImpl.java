package com.himedia.luckydokiapi.domain.product.repository.querydsl;


import com.himedia.luckydokiapi.domain.product.dto.ProductRequestDTO;
import com.himedia.luckydokiapi.domain.product.entity.Product;
import com.himedia.luckydokiapi.domain.product.enums.ProductMdPick;
import com.himedia.luckydokiapi.domain.product.enums.ProductNew;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


import static com.himedia.luckydokiapi.domain.product.entity.QProduct.product;

@Slf4j
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory queryFactory;


    @Override
    public List<Product> findByDTO(ProductRequestDTO requestDTO) {

        return queryFactory
                .selectFrom(product)
                .where(
                        eqCategory(requestDTO.getCategoryId()),
                        eqMdPick(requestDTO.getMdPick()),
                        containsSearchKeyword(requestDTO.getSearchKeyword())
//                        eqTag(productDTO.getTagStrList().get)
                )
                .orderBy(product.id.desc())
                .fetch();
    }


    private OrderSpecifier<?> eqIsNew(ProductNew isNew) {
        if (isNew == null || ProductNew.N.equals(isNew)) {
            return product.id.asc();
        }
        return product.id.desc();
    }

    private BooleanExpression eqCategory(Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        return product.category.id.eq(categoryId);
    }

    private BooleanExpression eqMdPick(ProductMdPick mdPick) {
        if (mdPick == null) {
            return null;
        }
        return product.mdPick.eq(mdPick);
    }


    private BooleanExpression containsSearchKeyword(String searchKeyword) {
        if (searchKeyword == null) {
            return null;
        }
        return product.name.contains(searchKeyword)
                .or(product.category.name.contains(searchKeyword));

    }


}

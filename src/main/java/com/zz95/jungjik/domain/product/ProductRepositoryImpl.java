package com.zz95.jungjik.domain.product;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zz95.jungjik.scraping.ScraperType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private static final QProduct product = QProduct.product;

    @Override
    public List<Product> findProductsNoOffset(Long lastId, int size, String keyword, ScraperType source) {
        return queryFactory
                .selectFrom(product)
                .where(
                        ltId(lastId),
                        keywordContains(keyword),
                        sourceEq(source)
                )
                .orderBy(product.id.desc())
                .limit(size + 1) // hasNext 판단을 위해 1개 더 조회
                .fetch();
    }

    private BooleanExpression ltId(Long lastId) {
        return lastId != null ? product.id.lt(lastId) : null;
    }

    private BooleanExpression keywordContains(String keyword) {
        return (keyword != null && !keyword.isBlank())
                ? product.name.containsIgnoreCase(keyword)
                : null;
    }

    private BooleanExpression sourceEq(ScraperType source) {
        return source != null ? product.source.eq(source) : null;
    }
}
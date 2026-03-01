package com.zz95.jungjik.domain.product;

import com.zz95.jungjik.scraping.ScraperType;

import java.util.List;

public interface ProductRepositoryCustom {

    /**
     * @param lastId   마지막으로 조회한 상품 ID
     * @param size     조회할 개수
     * @param keyword  상품명 검색어
     * @param source   상품 출처 필터
     */
    List<Product> findProductsNoOffset(Long lastId, int size, String keyword, ScraperType source);
}
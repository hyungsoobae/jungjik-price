package com.zz95.jungjik.domain.sort;

import org.springframework.data.domain.Sort;

public enum ProductSortType {
    LATEST(Sort.by(Sort.Direction.DESC, "id")),
    PRICE_ASC(Sort.by(Sort.Direction.ASC, "currentPrice")),
    PRICE_DESC(Sort.by(Sort.Direction.DESC, "currentPrice")),
    DIFF_RATE_ASC(Sort.by(Sort.Direction.ASC, "diffRate"));

    private final Sort sort;

    ProductSortType(Sort sort) {
        this.sort = sort;
    }

    public Sort getSort() {
        return sort;
    }
}
package com.zz95.jungjik.domain.product.dto;

public record ProductRegisterResult(
        Long productId,
        String name,
        Integer currentPrice,
        boolean isNew
) {
}
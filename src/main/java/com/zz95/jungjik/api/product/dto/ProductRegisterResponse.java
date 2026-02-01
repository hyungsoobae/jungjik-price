package com.zz95.jungjik.api.product.dto;

import com.zz95.jungjik.domain.product.dto.ProductRegisterResult;

public record ProductRegisterResponse(
        Long productId,
        String name,
        Integer currentPrice,
        boolean isNew
) {

    public static ProductRegisterResponse from(ProductRegisterResult result) {
        return new ProductRegisterResponse(
                result.productId(),
                result.name(),
                result.currentPrice(),
                result.isNew()
        );
    }
}
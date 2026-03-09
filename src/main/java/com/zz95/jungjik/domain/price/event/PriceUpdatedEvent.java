package com.zz95.jungjik.domain.price.event;

import com.zz95.jungjik.domain.price.dto.PriceUpdateResult;

public record PriceUpdatedEvent(
        int oldPrice,
        int newPrice,
        String productName,
        String productUrl
) {
    public static PriceUpdatedEvent from(PriceUpdateResult result) {
        return new PriceUpdatedEvent(
                result.oldPrice(),
                result.newPrice(),
                result.productName(),
                result.productUrl()
        );
    }
}
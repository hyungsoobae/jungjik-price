package com.zz95.jungjik.domain.price.dto;

import com.zz95.jungjik.domain.price.PriceHistory;

import java.time.LocalDateTime;

public record PriceHistoryResponse(
        Long id,
        Integer price,
        LocalDateTime collectedAt
) {
    public static PriceHistoryResponse from(PriceHistory history) {
        return new PriceHistoryResponse(
                history.getId(),
                history.getPrice(),
                history.getCollectedAt()
        );
    }
}

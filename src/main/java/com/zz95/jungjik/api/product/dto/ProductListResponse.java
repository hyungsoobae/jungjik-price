package com.zz95.jungjik.api.product.dto;

import com.zz95.jungjik.domain.product.Product;
import com.zz95.jungjik.scraping.ScraperType;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.time.LocalDateTime;

public record ProductListResponse(
        Long id,
        String name,
        ScraperType source,
        Integer currentPrice,
        Integer previousPrice,
        Integer priceDiff,
        Double diffRate,
        LocalDateTime priceChangedAt
) {
    public static ProductListResponse from(Product product) {
        Integer prev = product.getPreviousPrice();

        if (prev == null) {
            return new ProductListResponse(
                    product.getId(), product.getName(), product.getSource(),
                    product.getCurrentPrice(), null, null, null, null
            );
        }

        int diff = product.getCurrentPrice() - prev;
        double rate = Math.round(((double) diff / prev) * 1000) / 10.0;

        return new ProductListResponse(
                product.getId(), product.getName(), product.getSource(),
                product.getCurrentPrice(), prev, diff, rate, product.getPriceChangedAt()
        );
    }
}
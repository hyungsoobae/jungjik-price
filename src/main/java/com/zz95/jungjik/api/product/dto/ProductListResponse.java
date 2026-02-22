package com.zz95.jungjik.api.product.dto;

import com.zz95.jungjik.domain.product.Product;
import com.zz95.jungjik.scraping.ScraperType;

import java.time.LocalDateTime;

public record ProductListResponse(
        Long id,
        String name,
        ScraperType source,
        Integer currentPrice,
        Integer diffPrice,
        Double diffRate,
        LocalDateTime priceChangedAt
) {
    public static ProductListResponse from(Product product) {
        return new ProductListResponse(
                product.getId(),
                product.getName(),
                product.getSource(),
                product.getCurrentPrice(),
                product.getDiffPrice(),
                product.getDiffRate(),
                product.getPriceChangedAt()
        );
    }
}
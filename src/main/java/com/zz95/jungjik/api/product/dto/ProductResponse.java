package com.zz95.jungjik.api.product.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.zz95.jungjik.domain.product.Product;
import com.zz95.jungjik.scraping.ScraperType;

import java.time.LocalDateTime;

public record ProductResponse(
        @JsonSerialize(using = ToStringSerializer.class)
        Long id,
        String name,
        ScraperType source,
        Integer currentPrice,
        Integer diffPrice,
        Double diffRate,
        LocalDateTime priceChangedAt
) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(
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
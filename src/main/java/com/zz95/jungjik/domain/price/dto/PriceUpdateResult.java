package com.zz95.jungjik.domain.price.dto;

/**
 * 가격 업데이트 트랜잭션의 결과를 @Async 메서드로 전달하기 위한 DTO
 */
public record PriceUpdateResult(
        boolean isChanged,
        int oldPrice,
        int newPrice,
        String productName,
        String productUrl
) {
}
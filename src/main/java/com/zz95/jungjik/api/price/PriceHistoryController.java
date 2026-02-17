package com.zz95.jungjik.api.price;

import com.zz95.jungjik.domain.price.PriceHistoryService;
import com.zz95.jungjik.domain.price.dto.PriceHistoryResponse;
import com.zz95.jungjik.domain.product.ProductService;
import com.zz95.jungjik.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products/{productId}/price-histories")
public class PriceHistoryController {

    private final ProductService productService;
    private final PriceHistoryService priceHistoryService;

    /**
     * 상품 가격 이력 조회
     *
     * GET /api/products/{productId}/price-histories          → 전체
     * GET /api/products/{productId}/price-histories?days=7   → 최근 7일
     * GET /api/products/{productId}/price-histories?days=14  → 최근 14일
     * GET /api/products/{productId}/price-histories?days=30  → 최근 30일
     */
    @GetMapping
    public ApiResponse<List<PriceHistoryResponse>> getPriceHistories(
            @PathVariable Long productId,
            @RequestParam(required = false) Integer days
    ) {
        // 상품 존재 여부 확인 (없으면 ProductService 내부에서 BusinessException 발생)
        productService.getProduct(productId);

        List<PriceHistoryResponse> histories = (days != null && days > 0)
                ? priceHistoryService.getHistoriesSince(productId, days)
                : priceHistoryService.getHistories(productId);

        return ApiResponse.success(histories);
    }
}
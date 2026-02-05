package com.zz95.jungjik.api.admin;

import com.zz95.jungjik.domain.price.PriceHistoryService;
import com.zz95.jungjik.domain.product.Product;
import com.zz95.jungjik.domain.product.ProductRepository;
import com.zz95.jungjik.global.common.ApiResponse;
import com.zz95.jungjik.global.error.ErrorCode;
import com.zz95.jungjik.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/prices")
public class AdminPriceController {

    private final ProductRepository productRepository;
    private final PriceHistoryService priceHistoryService;

    /**
     * 전체 상품 가격 수집 (수동)
     */
    @PostMapping("/collect")
    public ApiResponse<Void> collectAll() {
        productRepository.findByIsActiveTrue()
                .forEach(priceHistoryService::collect);
        return ApiResponse.success();
    }

    /**
     * 단일 상품 가격 수집 (수동)
     */
    @PostMapping("/collect/{productId}")
    public void collectOne(@PathVariable Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
        priceHistoryService.collect(product);
    }
}

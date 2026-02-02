package com.zz95.jungjik.api.admin;

import com.zz95.jungjik.domain.price.PriceHistoryService;
import com.zz95.jungjik.domain.product.Product;
import com.zz95.jungjik.domain.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
    public void collectAll() {
        productRepository.findByIsActiveTrue()
                .forEach(priceHistoryService::collect);
    }

    /**
     * 단일 상품 가격 수집 (수동)
     */
    @PostMapping("/collect/{productId}")
    public void collectOne(@PathVariable Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow();
        priceHistoryService.collect(product);
    }
}

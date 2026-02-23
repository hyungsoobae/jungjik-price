package com.zz95.jungjik.schedule;

import com.zz95.jungjik.domain.price.PriceHistoryService;
import com.zz95.jungjik.domain.product.Product;
import com.zz95.jungjik.domain.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PriceCollectScheduler {

    private final ProductRepository productRepository;
    private final PriceHistoryService priceHistoryService;

    /**
     * 매일 12시에 활성화된 상품 가격 수집
     */
    @Scheduled(cron = "0 0 12 * * *")
    public void collectPrices() {
        log.info("[가격 수집 스케줄러] 시작");

        List<Product> activeProducts = productRepository.findByIsActiveTrue();
        log.info("[가격 수집 스케줄러] 대상 상품: {}개", activeProducts.size());

        activeProducts.forEach(product -> priceHistoryService.collect(product.getId()));

        log.info("[가격 수집 스케줄러] 종료");
    }
}
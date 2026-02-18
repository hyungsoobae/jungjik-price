package com.zz95.jungjik.schedule;

import com.zz95.jungjik.domain.price.PriceHistoryService;
import com.zz95.jungjik.domain.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PriceCollectScheduler {

    private final ProductRepository productRepository;
    private final PriceHistoryService priceHistoryService;

    /**
     * 매일 12에 활성화된 상품 가격 수집
     */
    @Scheduled(cron = "0 0 12 * * *")
    public void collectPrices() {
        log.info("PriceCollectScheduler.collectPrices : START ");

        productRepository.findByIsActiveTrue()
                .forEach(priceHistoryService::collect);
    }
}
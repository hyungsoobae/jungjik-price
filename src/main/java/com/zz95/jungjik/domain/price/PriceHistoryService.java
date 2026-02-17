package com.zz95.jungjik.domain.price;

import com.zz95.jungjik.domain.price.dto.PriceHistoryResponse;
import com.zz95.jungjik.domain.product.Product;
import com.zz95.jungjik.domain.product.ProductRepository;
import com.zz95.jungjik.global.slack.SlackClient;
import com.zz95.jungjik.global.slack.SlackMessageGenerator;
import com.zz95.jungjik.scraping.PriceScraper;
import com.zz95.jungjik.scraping.ScrapedProduct;
import com.zz95.jungjik.scraping.ScraperResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PriceHistoryService {

    private final PriceHistoryRepository priceHistoryRepository;
    private final ProductRepository productRepository;
    private final PriceUpdateService priceUpdateService;
    private final ScraperResolver scraperResolver;
    private final SlackClient slackClient;

    /**
     * 단일 상품 가격 수집
     */
    @Async("scrapingExecutor")
    public void collect(Product product) {
        log.info("[상품 가격 수집 시작] ProductId: {}, Thread: {}", product.getId(), Thread.currentThread().getName());

        PriceScraper scraper = scraperResolver.resolve(product.getProductUrl());
        ScrapedProduct scraped;

        try {
            scraped = scraper.scrape(product.getProductUrl());
        } catch (Exception e) {
            log.error("[상품 가격 수집 실패] ProductId: {}, URL: {}, error: {}",
                    product.getId(), product.getProductUrl(), e.getMessage(), e);

            slackClient.sendToAdmin(SlackMessageGenerator.getScrapingErrorNotice(product, e));
            return;
        }

        Integer oldPrice = product.getCurrentPrice();

        // Product.currentPrice 변동 확인, 업데이트
        boolean isChanged = priceUpdateService.updateProductAndSaveHistory(product, scraped);

        // 가격 변동 시 Slack 알림 발송
        if (isChanged) {
            slackClient.sendToUser(SlackMessageGenerator.getPriceNotice(product, oldPrice));
        }

        log.info("[상품 가격 수집 완료] ProductId: {}", product.getId());
    }

    /**
     * 상품 가격 이력 조회 (전체)
     */
    @Transactional(readOnly = true)
    public List<PriceHistoryResponse> getHistories(Long productId) {
        return priceHistoryRepository
                .findByProductIdOrderByCollectedAtAsc(productId)
                .stream()
                .map(PriceHistoryResponse::from)
                .toList();
    }

    /**
     * 상품 가격 이력 조회 (기간별)
     */
    @Transactional(readOnly = true)
    public List<PriceHistoryResponse> getHistoriesSince(Long productId, int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return priceHistoryRepository
                .findByProductIdAndCollectedAtAfterOrderByCollectedAtAsc(productId, since)
                .stream()
                .map(PriceHistoryResponse::from)
                .toList();
    }
}
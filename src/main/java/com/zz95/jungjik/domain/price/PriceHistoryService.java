package com.zz95.jungjik.domain.price;

import com.zz95.jungjik.domain.price.dto.PriceHistoryResponse;
import com.zz95.jungjik.domain.price.dto.PriceUpdateResult;
import com.zz95.jungjik.domain.product.Product;
import com.zz95.jungjik.domain.product.ProductRepository;
import com.zz95.jungjik.global.error.ErrorCode;
import com.zz95.jungjik.global.error.exception.BusinessException;
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
    public void collect(Long productId) {
        log.info("[상품 가격 수집 시작] productId={}, thread={}", productId, Thread.currentThread().getName());

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        PriceScraper scraper = scraperResolver.resolve(product.getProductUrl());
        ScrapedProduct scraped;

        try {
            scraped = scraper.scrape(product.getProductUrl());
        } catch (Exception e) {
            log.error("[상품 가격 수집 실패] productId={}, url={}, error={}",
                    productId, product.getProductUrl(), e.getMessage(), e);
            slackClient.sendToAdmin(SlackMessageGenerator.getScrapingErrorNotice(product, e));
            return;
        }

        // 별도 트랜잭션에서 가격 이력 저장 및 상품 가격 업데이트
        // 결과를 DTO로 받아 트랜잭션 종료 후에도 필요한 값을 안전하게 사용
        PriceUpdateResult result = priceUpdateService.updateProductAndSaveHistory(productId, scraped);

        if (result.isChanged()) {
            slackClient.sendToUser(SlackMessageGenerator.getPriceNotice(result));
        }

        log.info("[상품 가격 수집 완료] productId={}", productId);
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
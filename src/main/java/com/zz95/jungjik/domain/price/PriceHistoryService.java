package com.zz95.jungjik.domain.price;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PriceHistoryService {

    private final PriceHistoryRepository priceHistoryRepository;
    private final ProductRepository productRepository;
    private final ScraperResolver scraperResolver;
    private final SlackClient slackClient;

    /**
     * 단일 상품 가격 수집
     */
    public void collect(Product product) {

        PriceScraper scraper = scraperResolver.resolve(product.getProductUrl());

        ScrapedProduct scraped;
        try {
            scraped = scraper.scrape(product.getProductUrl());
        } catch (IOException e) {
            log.error("[상품 가격 수집 실패] 상품ID: {}, URL: {}, error: {}",
                    product.getId(), product.getProductUrl(), e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        // Product.currentPrice 변동 확인, 업데이트
        Integer oldPrice = product.getCurrentPrice();
        boolean isChanged = product.updateCurrentPrice(scraped.getPrice());

        // 가격 변동 시 Slack 알림 발송
        if (isChanged) {
            var message = SlackMessageGenerator.getPriceNotice(
                    product.getName(), oldPrice, scraped.getPrice(), product.getProductUrl()
            );
            slackClient.send(message);
            productRepository.save(product);
        }

        PriceHistory history = new PriceHistory(
                product,
                scraped.getPrice(),
                LocalDateTime.now()
        );

        priceHistoryRepository.save(history);
    }
}

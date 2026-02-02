package com.zz95.jungjik.domain.price;

import com.zz95.jungjik.domain.product.Product;
import com.zz95.jungjik.scraping.PriceScraper;
import com.zz95.jungjik.scraping.ScrapedProduct;
import com.zz95.jungjik.scraping.ScraperResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class PriceHistoryService {

    private final PriceHistoryRepository priceHistoryRepository;
    private final ScraperResolver scraperResolver;

    /**
     * 단일 상품 가격 수집
     */
    public void collect(Product product) {

        PriceScraper scraper =
                scraperResolver.resolve(product.getProductUrl());

        ScrapedProduct scraped;
        try {
            scraped = scraper.scrape(product.getProductUrl());
        } catch (IOException e) {
            // TODO: 로그 처리
            return;
        }

        PriceHistory history = new PriceHistory(
                product,
                scraped.getPrice(),
                LocalDateTime.now()
        );

        priceHistoryRepository.save(history);
    }
}

package com.zz95.jungjik.domain.product;

import com.zz95.jungjik.domain.product.dto.ProductRegisterResult;
import com.zz95.jungjik.scraping.PriceScraper;
import com.zz95.jungjik.scraping.ScrapedProduct;
import com.zz95.jungjik.scraping.ScraperResolver;
import com.zz95.jungjik.scraping.ScraperType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final ScraperResolver scraperResolver;

    /**
     * 추적 대상 상품 등록
     */
    public ProductRegisterResult register(String productUrl) {

        // 스크래퍼 선택
        PriceScraper scraper = scraperResolver.resolve(productUrl);
        ScraperType source = scraper.type();

        // 상품 정보 스크래핑
        ScrapedProduct scraped;
        try {
            scraped = scraper.scrape(productUrl);
        } catch (IOException e) {
            throw new IllegalStateException("상품 스크래핑 실패: " + productUrl, e);
        }

        // Product 조회
        Product product = productRepository
                .findBySourceAndExternalProductId(
                        source,
                        scraped.getProductId()
                )
                .orElse(null);

        boolean isNew = false;

        // 없으면 생성
        if (product == null) {
            product = productRepository.save(
                    new Product(
                            scraped.getProductId(),
                            source,
                            scraped.getName(),
                            productUrl
                    )
            );
            isNew = true;
        }

        // 결과 반환
        return new ProductRegisterResult(
                product.getId(),
                scraped.getName(),
                scraped.getPrice(),
                isNew
        );
    }
}
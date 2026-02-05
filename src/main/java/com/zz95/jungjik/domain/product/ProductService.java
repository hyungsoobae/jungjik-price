package com.zz95.jungjik.domain.product;

import com.zz95.jungjik.domain.product.dto.ProductRegisterResult;
import com.zz95.jungjik.global.error.ErrorCode;
import com.zz95.jungjik.global.error.exception.BusinessException;
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
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final ScraperResolver scraperResolver;

    /**
     * 추적 대상 상품 등록
     */
    public ProductRegisterResult registProduct(String productUrl) {

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

        return productRepository.findBySourceAndExternalProductId(source, scraped.getProductId())
                // product가 있을 때
                .map(product -> new ProductRegisterResult(
                        product.getId(),
                        product.getName(),
                        scraped.getPrice(),
                        false
                ))
                // product가 없을 때
                .orElseGet(() -> {
                    Product newProduct = productRepository.save(
                            new Product(scraped.getProductId(), source, scraped.getName(), productUrl)
                    );
                    return new ProductRegisterResult(
                            newProduct.getId(),
                            newProduct.getName(),
                            scraped.getPrice(),
                            true
                    );
                });
    }

    public Product getProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
        productRepository.delete(product);
    }
}
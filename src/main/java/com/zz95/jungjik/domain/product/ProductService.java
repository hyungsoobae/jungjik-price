package com.zz95.jungjik.domain.product;

import com.zz95.jungjik.api.product.dto.ProductListResponse;
import com.zz95.jungjik.domain.product.dto.ProductRegisterResult;
import com.zz95.jungjik.domain.sort.ProductSortType;
import com.zz95.jungjik.global.error.ErrorCode;
import com.zz95.jungjik.global.error.exception.BusinessException;
import com.zz95.jungjik.scraping.PriceScraper;
import com.zz95.jungjik.scraping.ScrapedProduct;
import com.zz95.jungjik.scraping.ScraperResolver;
import com.zz95.jungjik.scraping.ScraperType;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ScraperResolver scraperResolver;

    /**
     * 추적 대상 상품 등록
     */
    @Transactional
    public ProductRegisterResult registerProduct(String productUrl) {

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
                            new Product(scraped.getProductId(), source, scraped.getName(), productUrl, scraped.getPrice())
                    );
                    return new ProductRegisterResult(
                            newProduct.getId(),
                            newProduct.getName(),
                            scraped.getPrice(),
                            true
                    );
                });
    }

    /**
     * 상품 단건 조회
     */
    @Transactional(readOnly = true)
    public Product getProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    /**
     * 상품 목록 조회 (페이징, 검색, 정렬)
     */
    @Transactional(readOnly = true)
    public Page<ProductListResponse> getProductList(int page, int size, ProductSortType sortType, String keyword, ScraperType source) {
        Pageable pageable = PageRequest.of(page, size, sortType.getSort());

        boolean hasKeyword = keyword != null && !keyword.isBlank();
        boolean hasSource = source != null;

        Page<Product> products;

        if (hasSource && hasKeyword) {
            products = productRepository.findBySourceAndNameContainingIgnoreCase(source, keyword, pageable);
        } else if (hasSource) {
            products = productRepository.findBySource(source, pageable);
        } else if (hasKeyword) {
            products = productRepository.findByNameContainingIgnoreCase(keyword, pageable);
        } else {
            products = productRepository.findAll(pageable);
        }

        return products.map(ProductListResponse::from);
    }

    /**
     * 상품 삭제
     */
    @Transactional
    public void deleteProduct(Long id) {
        Product product = getProduct(id);
        productRepository.delete(product);
    }

    /**
     * 상품 존재 여부 확인
     */
    @Transactional(readOnly = true)
    public void validateProductExists(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND);
        }
    }
}
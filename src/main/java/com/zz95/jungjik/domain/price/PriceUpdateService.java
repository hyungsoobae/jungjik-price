package com.zz95.jungjik.domain.price;

import com.zz95.jungjik.domain.product.Product;
import com.zz95.jungjik.domain.product.ProductRepository;
import com.zz95.jungjik.scraping.ScrapedProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PriceUpdateService {
    private final ProductRepository productRepository;
    private final PriceHistoryRepository priceHistoryRepository;

    /**
     * 현재 최신 가격 업데이트 및 상품 가격 수집 이력 저장
     * @return 가격 변동이 있으면 true, 없으면 false
     */
    @Transactional
    public boolean updateProductAndSaveHistory(Product product, ScrapedProduct scraped) {
        // 가격 히스토리 저장
        priceHistoryRepository.save(new PriceHistory(product, scraped));

        // 상품 최신 가격 업데이트
        boolean isChanged = product.updateCurrentPrice(scraped);
        if (isChanged) {
            productRepository.save(product); // 명시적 save
        }
        return isChanged;
    }
}
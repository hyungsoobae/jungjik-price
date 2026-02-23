package com.zz95.jungjik.domain.price;

import com.zz95.jungjik.domain.price.dto.PriceUpdateResult;
import com.zz95.jungjik.domain.product.Product;
import com.zz95.jungjik.domain.product.ProductRepository;
import com.zz95.jungjik.global.error.ErrorCode;
import com.zz95.jungjik.global.error.exception.BusinessException;
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
     * @return PriceUpdateResult
     * 가격 이력 저장 및 상품 최신 가격 업데이트
     */
    @Transactional
    public PriceUpdateResult updateProductAndSaveHistory(Long productId, ScrapedProduct scraped) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        int oldPrice = product.getCurrentPrice();

        priceHistoryRepository.save(new PriceHistory(product, scraped));

        // 상품 최신 가격 업데이트
        boolean isChanged = product.updateCurrentPrice(scraped);
        if (isChanged) {
            productRepository.save(product); // 명시적 save
        }

        // 트랜잭션 종료 전에 필요한 값을 DTO에 담아 반환
        // 트랜잭션 종료 후 product는 detached 상태가 되므로 호출 측에서 직접 접근하지 않음
        return new PriceUpdateResult(
                isChanged,
                oldPrice,
                product.getCurrentPrice(),
                product.getName(),
                product.getProductUrl()
        );
    }
}
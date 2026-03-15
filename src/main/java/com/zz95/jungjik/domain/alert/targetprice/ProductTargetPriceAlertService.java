package com.zz95.jungjik.domain.alert.targetprice;

import com.zz95.jungjik.domain.product.Product;
import com.zz95.jungjik.domain.product.ProductRepository;
import com.zz95.jungjik.global.error.ErrorCode;
import com.zz95.jungjik.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductTargetPriceAlertService {

    private final ProductTargetPriceAlertRepository productTargetPriceAlertRepository;
    private final ProductRepository productRepository;

    /**
     * 목표가 설정,수정
     * 이미 설정된 목표가가 있으면 수정, 없으면 새로 생성
     */
    @Transactional
    public void upsertAlert(Long productId, Integer targetPrice) {
        productTargetPriceAlertRepository.findByProductId(productId)
                .ifPresentOrElse(
                        // 이미 존재하면 목표가 수정
                        alert -> alert.updateTargetPrice(targetPrice),
                        // 없으면 새로 생성
                        () -> {
                            Product product = productRepository.findById(productId)
                                    .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
                            productTargetPriceAlertRepository.save(new ProductTargetPriceAlert(product, targetPrice));
                        }
                );
    }

    /**
     * 목표가 삭제
     */
    @Transactional
    public void deleteAlert(Long productId) {
        ProductTargetPriceAlert alert = productTargetPriceAlertRepository.findByProductId(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_ALERT_NOT_FOUND));
        productTargetPriceAlertRepository.delete(alert);
    }
}
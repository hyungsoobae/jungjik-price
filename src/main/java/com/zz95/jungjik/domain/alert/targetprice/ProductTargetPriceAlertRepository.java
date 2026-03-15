package com.zz95.jungjik.domain.alert.targetprice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductTargetPriceAlertRepository extends JpaRepository<ProductTargetPriceAlert, Long> {

    /**
     * 상품 ID로 목표가 알림 설정 조회
     */
    Optional<ProductTargetPriceAlert> findByProductId(Long productId);

    /**
     * 상품 URL로 목표가 알림 설정 조회
     */
    Optional<ProductTargetPriceAlert> findByProductProductUrl(String productUrl);
}
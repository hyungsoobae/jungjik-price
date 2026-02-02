package com.zz95.jungjik.domain.product;

import com.zz95.jungjik.scraping.ScraperType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * source, externalProductId 로 상품 조회
     */
    Optional<Product> findBySourceAndExternalProductId(
            ScraperType source,
            String externalProductId
    );

    /**
     * 추적 활성화 상태의 상품 List 조회
     */
    List<Product> findByIsActiveTrue();
}
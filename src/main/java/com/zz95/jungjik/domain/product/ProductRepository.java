package com.zz95.jungjik.domain.product;

import com.zz95.jungjik.scraping.ScraperType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {

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

    /**
     * 상품 출처별 조회(페이징)
     */
    Page<Product> findBySource(ScraperType source, Pageable pageable);

    /**
     * 상품명 LIKE 검색(페이징)
     */
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);


    /**
     * 상품 출처별, 상품명 LIKE 검색(페이징)
     */
    Page<Product> findBySourceAndNameContainingIgnoreCase(ScraperType source, String keyword, Pageable pageable);

}
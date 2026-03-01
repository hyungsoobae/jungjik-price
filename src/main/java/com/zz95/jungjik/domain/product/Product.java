package com.zz95.jungjik.domain.product;

import com.zz95.jungjik.global.common.BaseTimeEntity;
import com.zz95.jungjik.scraping.ScrapedProduct;
import com.zz95.jungjik.scraping.ScraperType;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@NoArgsConstructor
@Entity
@Table(
        name = "product",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_product_source_external_id",
                        columnNames = {"source", "external_product_id"}
                )
        }
)
public class Product extends BaseTimeEntity {

    @Id
    @Tsid
    private Long id;

    /**
     * 외부 상품 ID
     */
    @Column(name = "external_product_id", nullable = false, length = 50)
    private String externalProductId;

    /**
     * 상품 출처
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ScraperType source;

    /**
     * 상품명
     */
    @Column(nullable = false, length = 255)
    private String name;

    /**
     * 상품 페이지 URL
     */
    @Column(name = "product_url", nullable = false, columnDefinition = "TEXT")
    private String productUrl;

    /**
     * 가격 추적 활성 여부
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    /**
     * 현재 최신 가격
     */
    @Column(name = "current_price", nullable = false)
    private Integer currentPrice;

    /**
     * 가장 최근 가격 변동 일시
     */
    @Column(name = "price_changed_at")
    private LocalDateTime priceChangedAt;

    /**
     * 가장 최근 가격 변동 등락폭
     */
    @Column(name = "diff_price")
    private Integer diffPrice;

    /**
     * 가장 최근 가격 변동 등락률
     */
    @Column(name = "diff_rate")
    private Double diffRate;

    public Product(
            String externalProductId,
            ScraperType source,
            String name,
            String productUrl,
            Integer currentPrice
    ) {
        this.externalProductId = externalProductId;
        this.source = source;
        this.name = name;
        this.productUrl = productUrl;
        this.currentPrice = currentPrice;
        this.isActive = true;
    }

    public boolean updateCurrentPrice(ScrapedProduct scraped) {
        if (Objects.equals(this.currentPrice, scraped.getPrice())) {
            return false;
        }
        int oldPrice = this.currentPrice;
        int newPrice = scraped.getPrice();
        int diff = newPrice - oldPrice;

        this.currentPrice = newPrice;
        this.diffPrice = diff;
        if (oldPrice > 0) {
            this.diffRate = Math.round(((double) diff / oldPrice) * 1000) / 10.0;
        } else {
            this.diffRate = 0.0; // 이전 가격이 0일 경우
        }
        this.priceChangedAt = LocalDateTime.now();

        return true;
    }
}

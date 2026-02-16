package com.zz95.jungjik.domain.product;

import com.zz95.jungjik.global.common.BaseTimeEntity;
import com.zz95.jungjik.scraping.ScrapedProduct;
import com.zz95.jungjik.scraping.ScraperType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
        this.currentPrice = scraped.getPrice();
        return true;
    }
}

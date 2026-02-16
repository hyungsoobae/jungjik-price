package com.zz95.jungjik.domain.price;

import com.zz95.jungjik.domain.product.Product;
import com.zz95.jungjik.scraping.ScrapedProduct;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "price_history")
public class PriceHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 가격 수집 대상 상품
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "product_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_price_history_product")
    )
    private Product product;

    /**
     * 가격
     */
    @Column(nullable = false)
    private Integer price;

    /**
     * 가격 수집 시각
     */
    @Column(name = "collected_at", nullable = false)
    private LocalDateTime collectedAt;

    public PriceHistory(
            Product product,
            ScrapedProduct scraped
    ) {
        this.product = product;
        this.price = scraped.getPrice();
        this.collectedAt = LocalDateTime.now();
    }
}

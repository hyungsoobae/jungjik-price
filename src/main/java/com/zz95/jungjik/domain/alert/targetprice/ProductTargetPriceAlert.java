package com.zz95.jungjik.domain.alert.targetprice;

import com.zz95.jungjik.domain.product.Product;
import com.zz95.jungjik.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(
        name = "product_target_price_alert",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_product_target_price_alert_product",
                        columnNames = {"product_id"}
                )
        }
)
public class ProductTargetPriceAlert extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 목표가 알림 대상 상품
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "product_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_product_target_price_alert_product")
    )
    private Product product;

    /**
     * 목표가
     */
    @Column(name = "target_price", nullable = false)
    private Integer targetPrice;

    public ProductTargetPriceAlert(Product product, Integer targetPrice) {
        this.product = product;
        this.targetPrice = targetPrice;
    }

    /**
     * 목표가 수정
     */
    public void updateTargetPrice(Integer targetPrice) {
        this.targetPrice = targetPrice;
    }

    /**
     * 현재 가격이 목표가 이하인지 확인
     */
    public boolean isTargetReached(int currentPrice) {
        return currentPrice <= this.targetPrice;
    }
}
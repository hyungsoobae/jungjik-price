package com.zz95.jungjik.global.slack;

import com.zz95.jungjik.domain.price.event.PriceUpdatedEvent;
import com.zz95.jungjik.domain.product.Product;
import com.zz95.jungjik.global.slack.dto.SlackMessageRequest;

import java.util.List;

public class SlackMessageGenerator {

    private SlackMessageGenerator() {
        // 의도치 않은 인스턴스화 방지
    }

    /**
     * 가격 변동 notice (사용자)
     * 이 메서드는 @Async 메소드(트랜잭션 밖)에서 호출되고
     * 트랜잭션이 종료된 시점에 Product 엔티티는 detached 상태이므로
     * PriceUpdateResult DTO 사용
     */
    public static SlackMessageRequest getPriceNotice(PriceUpdatedEvent event) {
        int oldPrice = event.oldPrice();
        int newPrice = event.newPrice();
        String color = (newPrice < oldPrice) ? "#36a64f" : "#ff0000";
        String emoji = (newPrice < oldPrice) ? "📉 가격 하락 알림!" : "📈 가격 변동 알림";
        String rateText = oldPrice > 0
                ? String.format("%+.1f%%", ((double) (newPrice - oldPrice) / oldPrice) * 100)
                : "";

        return new SlackMessageRequest(List.of(
                new SlackMessageRequest.Attachment(
                        color,
                        emoji,
                        event.productName(),
                        event.productUrl(),
                        String.format("기존가: %,d원\n*변경가: %,d원 (%s)*", oldPrice, newPrice, rateText),
                        "정직한 가격 추적기",
                        System.currentTimeMillis() / 1000,
                        null
                )
        ));
    }

    /**
     * 스크래핑 실패 notice (admin)
     */
    public static SlackMessageRequest getScrapingErrorNotice(Product product, Exception e) {
        return new SlackMessageRequest(List.of(
                new SlackMessageRequest.Attachment(
                        "#ff0000",
                        null,
                        "🚨 스크래핑 실패 알림!",
                        null,
                        null,
                        null,
                        System.currentTimeMillis() / 1000,
                        List.of(
                                new SlackMessageRequest.Field("상품 정보", "id : " + product.getId() + ", url : " + product.getProductUrl(), false),
                                new SlackMessageRequest.Field("에러 내용", e.getClass().getSimpleName() + " : " + e.getMessage(), false)
                        )
                )
        ));
    }
}
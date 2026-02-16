package com.zz95.jungjik.global.slack;

import com.zz95.jungjik.domain.product.Product;
import com.zz95.jungjik.global.slack.dto.SlackMessageRequest;

import java.util.List;

public class SlackMessageGenerator {

    /**
     * 가격 변동 notice (사용자)
     */
    public static SlackMessageRequest getPriceNotice(Product product, int oldPrice) {
        int newPrice = product.getCurrentPrice();
        String color = (newPrice < oldPrice) ? "#36a64f" : "#ff0000";
        String emoji = (newPrice < oldPrice) ? "📉 가격 하락 알림!" : "📈 가격 변동 알림";
        String rateText = oldPrice > 0
                ? String.format("%+.1f%%", ((double) (newPrice - oldPrice) / oldPrice) * 100)
                : "";

        return new SlackMessageRequest(List.of(
                new SlackMessageRequest.Attachment(
                        color,
                        emoji,
                        product.getName(),
                        product.getProductUrl(),
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
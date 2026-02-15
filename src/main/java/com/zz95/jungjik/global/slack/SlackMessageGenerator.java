package com.zz95.jungjik.global.slack;

import com.zz95.jungjik.domain.product.Product;

import java.util.List;
import java.util.Map;

public class SlackMessageGenerator {


    /**
     * 가격 변동 notice (사용자)
     */
    public static Map<String, Object> getPriceNotice(Product product, int oldPrice) {
        int newPrice = product.getCurrentPrice();
        double changeRate = ((double) (newPrice - oldPrice) / oldPrice) * 100;

        // 가격 하락/상승에 따른 설정값
        String color = (newPrice < oldPrice) ? "#36a64f" : "#ff0000";
        String emoji = (newPrice < oldPrice) ? "📉 가격 하락 알림!" : "📈 가격 변동 알림";
        String rateText = String.format("%+.1f%%", changeRate);

        // Slack Attachment 구조 생성
        return Map.of(
                "attachments", List.of(
                        Map.of(
                                "color", color,
                                "pretext", emoji,
                                "title", product.getName(),
                                "title_link", product.getProductUrl(),
                                "text", String.format("기존가: %,d원\n*변경가: %,d원 (%s)*",
                                        oldPrice, newPrice, rateText),
                                "footer", "정직한 가격 추적기",
                                "ts", System.currentTimeMillis() / 1000
                        )
                )
        );
    }

    /**
     * 스크래핑 실패 notice (admin)
     */
    public static Map<String, Object> getScrapingErrorNotice(Product product, Exception e) {
        return Map.of(
                "attachments", List.of(
                        Map.of(
                                "color", "#ff0000",
                                "title", "🚨 스크래핑 실패 알림!",
                                "fields", List.of(
                                        Map.of("title", "상품 정보",
                                                "value", "id : " + product.getId() + ", url : " + product.getProductUrl(),
                                                "short", false),
                                        Map.of("title", "에러 내용",
                                                "value", e.getClass().getSimpleName() + " : " + e.getMessage(),
                                                "short", false)
                                ),
                                "ts", System.currentTimeMillis() / 1000
                        )
                )
        );
    }
}
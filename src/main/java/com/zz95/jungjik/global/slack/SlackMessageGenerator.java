package com.zz95.jungjik.global.slack;

import java.util.List;
import java.util.Map;

public class SlackMessageGenerator {

    public static Map<String, Object> getPriceNotice(String productName, int oldPrice, int newPrice, String url) {
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
                                "title", productName,
                                "title_link", url,
                                "text", String.format("기존가: %,d원\n*변경가: %,d원 (%s)*",
                                        oldPrice, newPrice, rateText),
                                "footer", "정직한 가격 추적기",
                                "ts", System.currentTimeMillis() / 1000
                        )
                )
        );
    }
}
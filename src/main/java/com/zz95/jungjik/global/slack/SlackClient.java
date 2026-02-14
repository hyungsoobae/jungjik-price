package com.zz95.jungjik.global.slack;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Slf4j
@Component
public class SlackClient {

    private final RestClient restClient;
    private final String webhookUrl;

    public SlackClient(@Value("${slack.webhook.url}") String webhookUrl) {
        this.restClient = RestClient.create();
        this.webhookUrl = webhookUrl;
    }

    /**
     * 공통 전송 메서드
     */
    public void send(Map<String, Object> payload) {
        try {
            restClient.post()
                    .uri(webhookUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.error("Slack 메시지 발송 실패: {}", e.getMessage());
        }
    }
}
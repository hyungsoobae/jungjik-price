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

    private enum ChannelType { USER, ADMIN }

    private final RestClient restClient;
    private final String userWebhookUrl;
    private final String adminWebhookUrl;

    public SlackClient(
            @Value("${slack.webhook.user-url}") String userWebhookUrl,
            @Value("${slack.webhook.admin-url}") String adminWebhookUrl
    ) {
        this.restClient = RestClient.create();
        this.userWebhookUrl = userWebhookUrl;
        this.adminWebhookUrl = adminWebhookUrl;
    }

    /**
     * 사용자 전송 메서드
     */
    public void sendToUser(Map<String, Object> payload) {
        sendToUrl(userWebhookUrl, ChannelType.USER, payload);
    }

    /**
     * 관리자 전송 메서드
     */
    public void sendToAdmin(Map<String, Object> payload) {
        sendToUrl(adminWebhookUrl, ChannelType.ADMIN, payload);
    }

    /**
     * 공통 전송 메서드
     */
    private void sendToUrl(String url, ChannelType channelType, Map<String, Object> payload) {
        try {
            restClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.error("Slack 메시지 발송 실패 ({}): {}", channelType, e.getMessage());
        }
    }
}
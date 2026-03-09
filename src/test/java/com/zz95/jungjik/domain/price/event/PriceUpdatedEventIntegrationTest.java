package com.zz95.jungjik.domain.price.event;

import com.zz95.jungjik.global.slack.SlackClient;
import com.zz95.jungjik.global.slack.dto.SlackMessageRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@SpringBootTest
class PriceUpdatedEventIntegrationTest {

    @Autowired
    private ApplicationEventPublisher eventPublisher; // 실제 ApplicationEventPublisher

    @MockitoBean
    private SlackClient slackClient; // Mock SlackClient

    @Test
    @DisplayName("PriceUpdatedEvent 발행 시 리스너가 이벤트를 수신해 Slack 알림을 발송한다")
    void publishEvent_리스너가이벤트수신후_Slack알림발송() {
        // given
        PriceUpdatedEvent event = new PriceUpdatedEvent(
                20000,
                10000,
                "상품명",
                "https://musinsa.com/products/1"
        );

        // when
        // 실제 Spring 컨텍스트에 이벤트 발행
        eventPublisher.publishEvent(event);

        // then
        // 리스너가 이벤트를 받아서 SlackClient를 호출했는지 검증
        verify(slackClient).sendToUser(any(SlackMessageRequest.class));
    }
}
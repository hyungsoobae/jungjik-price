package com.zz95.jungjik.domain.alert.targetprice;

import com.zz95.jungjik.domain.price.event.PriceUpdatedEvent;
import com.zz95.jungjik.global.slack.SlackClient;
import com.zz95.jungjik.global.slack.SlackMessageGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductTargetPriceAlertEventListener {

    private final ProductTargetPriceAlertRepository productTargetPriceAlertRepository;
    private final SlackClient slackClient;

    /**
     * PriceUpdatedEvent 구독
     * 목표가 설정이 없는 상품은 무시
     * 목표가 이하로 내려간 경우에만 Slack 알림 발송
     */
    @EventListener
    public void onPriceUpdated(PriceUpdatedEvent event) {
        productTargetPriceAlertRepository.findByProductProductUrl(event.productUrl())
                .filter(alert -> alert.isTargetReached(event.newPrice()))
                .ifPresent(alert ->
                        slackClient.sendToUser(SlackMessageGenerator.getTargetPriceNotice(alert, event))
                );
    }
}
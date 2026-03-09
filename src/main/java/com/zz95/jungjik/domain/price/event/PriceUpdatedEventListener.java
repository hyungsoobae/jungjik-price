package com.zz95.jungjik.domain.price.event;

import com.zz95.jungjik.global.slack.SlackClient;
import com.zz95.jungjik.global.slack.SlackMessageGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PriceUpdatedEventListener {

    private final SlackClient slackClient;

    @EventListener
    public void onPriceUpdated(PriceUpdatedEvent event) {
        slackClient.sendToUser(SlackMessageGenerator.getPriceNotice(event));
    }
}
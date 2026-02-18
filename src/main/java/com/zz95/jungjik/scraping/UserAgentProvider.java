package com.zz95.jungjik.scraping;

import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Setter
@Component
@ConfigurationProperties(prefix = "scraping")
public class UserAgentProvider {

    private List<String> userAgents;

    public String getRandomUserAgent() {
        if (userAgents == null || userAgents.isEmpty()) {
            throw new IllegalStateException("scraping.user-agents 설정이 누락되었습니다.");
        }
        int randomIndex = ThreadLocalRandom.current().nextInt(userAgents.size());
        return userAgents.get(randomIndex);
    }
}
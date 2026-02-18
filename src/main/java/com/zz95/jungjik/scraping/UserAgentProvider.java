package com.zz95.jungjik.scraping;

import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Setter
@Component
@ConfigurationProperties(prefix = "scraping")
public class UserAgentProvider {

    private List<String> userAgents;
    private final Random random = new Random();

    public String getRandomUserAgent() {
        return userAgents.get(random.nextInt(userAgents.size()));
    }
}
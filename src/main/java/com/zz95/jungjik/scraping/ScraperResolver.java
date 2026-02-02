package com.zz95.jungjik.scraping;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ScraperResolver {

    private final List<PriceScraper> scrapers;

    public PriceScraper resolve(String url) {
        return scrapers.stream()
                .filter(scraper -> scraper.supports(url))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("지원하지 않는 URL")
                );
    }
}
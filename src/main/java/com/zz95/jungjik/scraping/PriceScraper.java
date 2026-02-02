package com.zz95.jungjik.scraping;

import java.io.IOException;

public interface PriceScraper {
    /**
     * 스크래퍼가 처리할 수 있는 URL인지 여부 확인
     */
    boolean supports(String url);

    /**
     * 쇼핑몰 식별
     */
    ScraperType type();

    /**
     * 상품 정보 스크래핑
     */
    ScrapedProduct scrape(String url) throws IOException;
}

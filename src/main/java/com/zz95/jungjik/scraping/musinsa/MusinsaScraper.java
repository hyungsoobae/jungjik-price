package com.zz95.jungjik.scraping.musinsa;

import com.zz95.jungjik.scraping.PriceScraper;
import com.zz95.jungjik.scraping.ScrapedProduct;
import com.zz95.jungjik.scraping.ScraperType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

@Component
public class MusinsaScraper implements PriceScraper {

    private static final String MUSINSA_DOMAIN = "musinsa.com";

    @Override
    public boolean supports(String url) {
        try {
            java.net.URI uri = new java.net.URI(url);
            String host = uri.getHost();
            return host != null && (host.equals("www." + MUSINSA_DOMAIN) || host.equals(MUSINSA_DOMAIN));
        } catch (java.net.URISyntaxException e) {
            return false;
        }
    }

    @Override
    public ScraperType type() {
        return ScraperType.MUSINSA;
    }

    @Override
    public ScrapedProduct scrape(String url) throws IOException {

        Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0")
                .timeout(5000)
                .get();

        // 상품 ID 추출
        String productId = extractProductId(url);

        // 가격
        Element priceMeta = doc.selectFirst("meta[property=product:price:amount]");

        if (Objects.isNull(priceMeta)) {
            throw new IllegalStateException("무신사 가격 요소를 찾을 수 없음");
        }

        int price = Integer.parseInt(priceMeta.attr("content"));

        // 상품명
        Element nameMeta = doc.selectFirst("meta[property=og:title]");
        String name = nameMeta != null ? nameMeta.attr("content") : "unknown";

        return new ScrapedProduct(
                productId,
                name,
                price,
                url
        );
    }

    private String extractProductId(String url) {
        // ex) https://www.musinsa.com/products/4105642
        try {
            java.net.URI uri = new java.net.URI(url);
            String path = uri.getPath();

            if (path == null || path.isEmpty()) {
                throw new IllegalArgumentException("URL 경로가 비어있습니다: " + url);
            }

            // 경로의 마지막 세그먼트 추출
            String productId = path.substring(path.lastIndexOf("/") + 1);

            if (productId.isEmpty()) {
                throw new IllegalArgumentException("상품 ID를 추출할 수 없습니다: " + url);
            }

            return productId;
        } catch (java.net.URISyntaxException e) {
            throw new IllegalArgumentException("잘못된 URL 형식입니다: " + url, e);
        }
    }
}

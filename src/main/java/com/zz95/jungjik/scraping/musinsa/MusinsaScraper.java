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
    @Override
    public boolean supports(String url) {
        try {
            java.net.URI uri = new java.net.URI(url);
            String host = uri.getHost();
            return host != null && (host.equals("www.musinsa.com") || host.equals("musinsa.com"));
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
        // https://www.musinsa.com/products/4105642
        return url.substring(url.lastIndexOf("/") + 1);
    }
}

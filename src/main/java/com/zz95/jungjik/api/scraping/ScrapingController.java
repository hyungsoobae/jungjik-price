package com.zz95.jungjik.api.scraping;

import com.zz95.jungjik.global.common.ApiResponse;
import com.zz95.jungjik.scraping.PriceScraper;
import com.zz95.jungjik.scraping.ScrapedProduct;
import com.zz95.jungjik.scraping.ScraperResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class ScrapingController {

    private final ScraperResolver scraperResolver;

    @GetMapping("/scrape")
    public ApiResponse<ScrapedProduct> scrape(@RequestParam String url) throws IOException {
        PriceScraper scraper = scraperResolver.resolve(url);
        return ApiResponse.success(scraper.scrape(url));
    }
}
package com.zz95.jungjik.scraping;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ScrapedProduct {

    private final String productId;
    private final String name;
    private final int price;
    private final String productUrl;
}

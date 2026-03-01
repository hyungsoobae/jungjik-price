package com.zz95.jungjik.api.product.dto;

import java.util.List;

public record ProductListResponse(
        List<ProductResponse> content,
        boolean hasNext
) {
}
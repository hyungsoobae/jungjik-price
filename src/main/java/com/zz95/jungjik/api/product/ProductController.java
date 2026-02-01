package com.zz95.jungjik.api.product;

import com.zz95.jungjik.api.product.dto.ProductRegisterRequest;
import com.zz95.jungjik.api.product.dto.ProductRegisterResponse;
import com.zz95.jungjik.domain.product.ProductService;
import com.zz95.jungjik.domain.product.dto.ProductRegisterResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ProductRegisterResponse register(
            @RequestBody @Valid ProductRegisterRequest request
    ) {
        ProductRegisterResult result =
                productService.register(request.productUrl());

        return ProductRegisterResponse.from(result);
    }
}
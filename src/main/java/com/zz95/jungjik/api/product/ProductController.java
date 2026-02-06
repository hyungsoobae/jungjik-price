package com.zz95.jungjik.api.product;

import com.zz95.jungjik.api.product.dto.ProductRegisterRequest;
import com.zz95.jungjik.domain.product.Product;
import com.zz95.jungjik.domain.product.ProductService;
import com.zz95.jungjik.domain.product.dto.ProductRegisterResult;
import com.zz95.jungjik.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;


    /**
     * 상품 등록
     */
    @PostMapping
    public ApiResponse<ProductRegisterResult> registerProduct(@RequestBody @Valid ProductRegisterRequest request) {
        ProductRegisterResult result = productService.registerProduct(request.productUrl());
        return ApiResponse.success(result);
    }

    /**
     * 상품 상세 조회
     */
    @GetMapping("/{id}")
    public ApiResponse<Product> getProduct(@PathVariable Long id) {
        Product product = productService.getProduct(id);
        return ApiResponse.success(product);
    }

    /**
     * 상품 삭제
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ApiResponse.success();
    }
}
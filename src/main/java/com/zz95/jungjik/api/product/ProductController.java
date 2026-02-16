package com.zz95.jungjik.api.product;

import com.zz95.jungjik.api.product.dto.ProductRegisterRequest;
import com.zz95.jungjik.domain.product.Product;
import com.zz95.jungjik.domain.product.ProductService;
import com.zz95.jungjik.domain.product.dto.ProductRegisterResult;
import com.zz95.jungjik.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
     * 상품 목록 조회
     */
    @GetMapping
    public ApiResponse<Page<Product>> getProductList(@PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Product> products = productService.getProductList(pageable);
        return ApiResponse.success(products);
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
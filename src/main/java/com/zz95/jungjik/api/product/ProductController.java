package com.zz95.jungjik.api.product;

import com.zz95.jungjik.api.product.dto.ProductListResponse;
import com.zz95.jungjik.api.product.dto.ProductRegisterRequest;
import com.zz95.jungjik.domain.product.Product;
import com.zz95.jungjik.domain.product.ProductService;
import com.zz95.jungjik.domain.product.dto.ProductRegisterResult;
import com.zz95.jungjik.domain.sort.ProductSortType;
import com.zz95.jungjik.global.common.ApiResponse;
import com.zz95.jungjik.scraping.ScraperType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
    public ApiResponse<ProductListResponse> getProductList(
            @RequestParam(defaultValue = "10") @Max(value = 100, message = "size는 최대 100까지 가능합니다.") int size,
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "LATEST") ProductSortType sort,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) ScraperType source) {
        return ApiResponse.success(productService.getProductList(size, lastId, page, sort, keyword, source));
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
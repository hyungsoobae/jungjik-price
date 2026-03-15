package com.zz95.jungjik.api.alert.targetprice;

import com.zz95.jungjik.api.alert.targetprice.dto.ProductTargetPriceAlertRequest;
import com.zz95.jungjik.domain.alert.targetprice.ProductTargetPriceAlertService;
import com.zz95.jungjik.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products/{productId}/target-price-alert")
public class ProductTargetPriceAlertController {

    private final ProductTargetPriceAlertService productTargetPriceAlertService;

    /**
     * 목표가 설정,수정
     */
    @PutMapping
    public ApiResponse<Void> upsertAlert(
            @PathVariable Long productId,
            @RequestBody @Valid ProductTargetPriceAlertRequest request
    ) {
        productTargetPriceAlertService.upsertAlert(productId, request.targetPrice());
        return ApiResponse.success();
    }

    /**
     * 목표가 삭제
     */
    @DeleteMapping
    public ApiResponse<Void> deleteAlert(@PathVariable Long productId) {
        productTargetPriceAlertService.deleteAlert(productId);
        return ApiResponse.success();
    }
}
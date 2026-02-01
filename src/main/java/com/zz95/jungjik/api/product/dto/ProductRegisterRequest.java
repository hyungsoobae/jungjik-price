package com.zz95.jungjik.api.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProductRegisterRequest(

        @NotBlank(message = "상품 URL은 필수입니다.")
        @Size(max = 500, message = "상품 URL은 500자를 초과할 수 없습니다.")
        String productUrl

) {}
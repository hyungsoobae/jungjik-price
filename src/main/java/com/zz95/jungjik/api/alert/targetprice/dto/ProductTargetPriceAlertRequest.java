package com.zz95.jungjik.api.alert.targetprice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ProductTargetPriceAlertRequest(
        @NotNull(message = "목표가는 필수입니다.")
        @Min(value = 1, message = "목표가는 1원 이상이어야 합니다.")
        Integer targetPrice
) {}
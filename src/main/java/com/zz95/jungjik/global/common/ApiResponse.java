package com.zz95.jungjik.global.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.zz95.jungjik.global.error.ErrorCode;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        String status,
        T data,
        String message,
        String code
) {
    // 성공 응답 (데이터가 있는 경우)
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("success", data, null, null);
    }

    // 성공 응답 (데이터가 없는 경우)
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>("success", null, null, null);
    }

    // 실패 응답
    public static ApiResponse<Void> error(ErrorCode errorCode) {
        return new ApiResponse<>("error", null, errorCode.getMessage(), errorCode.getCode());
    }

    // 실패 응답 (상세 메시지를 직접 넣는 경우 - @Valid 예외 등)
    public static ApiResponse<Void> error(ErrorCode errorCode, String message) {
        return new ApiResponse<>("error", null, message, errorCode.getCode());
    }
}
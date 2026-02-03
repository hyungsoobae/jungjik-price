package com.zz95.jungjik.global.error;

import lombok.Getter;

@Getter
public enum ErrorCode {

    // Common
    INVALID_INPUT_VALUE(400, "C001", "올바르지 않은 입력값입니다."),
    METHOD_NOT_ALLOWED(405, "C002", "허용되지 않은 메소드입니다."),
    INTERNAL_SERVER_ERROR(500, "C003", "서버 내부 오류가 발생했습니다."),

    // Product
    PRODUCT_NOT_FOUND(404, "P001", "해당 상품을 찾을 수 없습니다."),
    INVALID_PRODUCT_URL(400, "P002", "잘못된 상품 URL 형식입니다.");

    private final int status;
    private final String code;
    private final String message;

    ErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
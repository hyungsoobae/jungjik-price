package com.zz95.jungjik.global.common;

import com.zz95.jungjik.global.error.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse<T> {

    private String status;
    private T data;
    private String message;
    private String code;

    // 성공 응답 (데이터가 있는 경우)
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.status = "success";
        response.data = data;
        return response;
    }

    // 성공 응답 (데이터가 없는 경우)
    public static <T> ApiResponse<T> success() {
        return success(null);
    }

    // 실패 응답
    public static ApiResponse<Void> error(ErrorCode errorCode) {
        ApiResponse<Void> response = new ApiResponse<>();
        response.status = "error";
        response.code = errorCode.getCode();
        response.message = errorCode.getMessage();
        return response;
    }

    // 실패 응답 (직접 메시지를 넣는 경우 - Validation...)
    public static ApiResponse<Void> error(ErrorCode errorCode, String message) {
        ApiResponse<Void> response = new ApiResponse<>();
        response.status = "error";
        response.code = errorCode.getCode();
        response.message = message;
        return response;
    }
}
package com.zz95.jungjik.global.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Objects;

@Component
public class AdminInterceptor implements HandlerInterceptor {
    @Value("${admin.api.token}")
    private String adminApiToken;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = request.getHeader("X-Admin-Token");

        if (isValid(token)) {
            return true;
        }

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        return false;
    }

    private boolean isValid(String token) {
        if (Objects.isNull(adminApiToken) || adminApiToken.isBlank() || Objects.isNull(token)) {
            return false;
        }

        return MessageDigest.isEqual(
                adminApiToken.getBytes(StandardCharsets.UTF_8),
                token.getBytes(StandardCharsets.UTF_8)
        );
    }
}
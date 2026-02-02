package com.zz95.jungjik.global.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminInterceptor implements HandlerInterceptor {
    @Value("${admin.api.token}")
    private String adminApiToken;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = request.getHeader("X-Admin-Token");
        if (adminApiToken.equals(token)) {
            return true;
        }
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        return false;
    }
}
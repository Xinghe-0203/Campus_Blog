package com.example.edu_project.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 简易频率限制拦截器（基于 Caffeine 本地缓存，单实例适用）
 * 生产环境建议使用 Redis + Lua 脚本实现分布式限流
 */
@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final Cache<String, AtomicInteger> requestCounts = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .maximumSize(10000)
            .build();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        String path = request.getRequestURI();
        String ip = getClientIp(request);

        // 仅对敏感接口启用限流
        int maxRequests;
        if (path.contains("/user/login")) {
            maxRequests = 10;
        } else if (path.contains("/user/register")) {
            maxRequests = 5;
        } else if (path.contains("/user/send-code")) {
            maxRequests = 3;
        } else if (path.contains("/user/reset-password")) {
            maxRequests = 5;
        } else {
            return true;
        }

        String key = ip + ":" + path;
        AtomicInteger count = requestCounts.get(key, k -> new AtomicInteger(0));
        int current = count.incrementAndGet();

        if (current > maxRequests) {
            response.setStatus(429);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":429,\"message\":\"请求过于频繁，请稍后再试\"}");
            return false;
        }
        return true;
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}

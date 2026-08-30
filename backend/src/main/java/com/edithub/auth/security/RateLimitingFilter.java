package com.edithub.auth.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Rate limiting filter for sensitive endpoints:
 * - /api/v1/auth/login (15 requests per minute per IP)
 * - /api/v1/auth/register (10 requests per minute per IP)
 * - /api/v1/projects/media/upload-url (30 requests per minute per IP)
 */
@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private static class RateWindow {
        final long startTimeMs = System.currentTimeMillis();
        final AtomicInteger count = new AtomicInteger(0);
    }

    private final Map<String, RateWindow> ipRateMap = new ConcurrentHashMap<>();

    private static final int MAX_LOGIN_PER_MINUTE = 15;
    private static final int MAX_REGISTER_PER_MINUTE = 10;
    private static final int MAX_UPLOAD_URL_PER_MINUTE = 30;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();
        String clientIp = getClientIP(request);

        int maxAllowed = -1;
        if (uri.endsWith("/auth/login")) {
            maxAllowed = MAX_LOGIN_PER_MINUTE;
        } else if (uri.endsWith("/auth/register")) {
            maxAllowed = MAX_REGISTER_PER_MINUTE;
        } else if (uri.contains("/media/upload-url")) {
            maxAllowed = MAX_UPLOAD_URL_PER_MINUTE;
        }

        if (maxAllowed > 0) {
            String key = clientIp + ":" + uri;
            long now = System.currentTimeMillis();

            RateWindow window = ipRateMap.compute(key, (k, current) -> {
                if (current == null || (now - current.startTimeMs > 60000)) {
                    return new RateWindow();
                }
                return current;
            });

            if (window.count.incrementAndGet() > maxAllowed) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json");
                response.getWriter().write("{\"success\":false,\"message\":\"Too many requests. Please slow down and try again later.\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null && !xfHeader.isBlank()) {
            return xfHeader.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}

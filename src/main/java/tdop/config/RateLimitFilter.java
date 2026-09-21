package tdop.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private static final int MAX_REQUESTS_PER_MINUTE = 20;
    private final Map<String, RateWindow> windows = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();
        if (path.startsWith("/api/v1/auth/login") && "POST".equals(request.getMethod())) {
            String clientIp = getClientIp(request);
            RateWindow window = windows.compute(clientIp, (k, v) -> {
                if (v == null || v.isExpired()) {
                    return new RateWindow();
                }
                return v;
            });
            if (window.incrementAndGet() > MAX_REQUESTS_PER_MINUTE) {
                log.warn("Rate limit exceeded for IP: {}", clientIp);
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Too many login attempts. Please try again later.\"}");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private static class RateWindow {
        private final long windowStart = System.currentTimeMillis();
        private final AtomicInteger count = new AtomicInteger(0);

        int incrementAndGet() {
            return count.incrementAndGet();
        }

        boolean isExpired() {
            return System.currentTimeMillis() - windowStart > 60_000;
        }
    }
}

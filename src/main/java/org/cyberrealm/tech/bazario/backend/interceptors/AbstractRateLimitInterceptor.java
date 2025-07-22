package org.cyberrealm.tech.bazario.backend.interceptors;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.exception.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

@RequiredArgsConstructor
public abstract class AbstractRateLimitInterceptor implements HandlerInterceptor {
    public static final int NUM_TOKENS_OF_PROBE = 1;
    public static final int DIVISOR_TO_SECONDS = 1_000_000_000;
    private final ObjectMapper mapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        var ip = request.getRemoteAddr();
        var uri = request.getRequestURI();
        if (ip == null || uri == null || ip.isBlank() || uri.isBlank()) {
            return false;
        }
        var bucket = getBucket(ip, uri);
        var probe = bucket.tryConsumeAndReturnRemaining(NUM_TOKENS_OF_PROBE);
        if (probe.isConsumed()) {
            response.addHeader("X-Rate-Limit-Remaining",
                    String.valueOf(probe.getRemainingTokens()));
            return true;
        } else {
            long waitForRefill = probe.getNanosToWaitForRefill() / DIVISOR_TO_SECONDS;
            response.addHeader("X-Rate-Limit-Retry-After-Seconds", String.valueOf(waitForRefill));
            var error = new ErrorResponse("You have exhausted your API Request Quota",
                    LocalDateTime.now());
            var message = mapper.writeValueAsString(error);
            response.sendError(HttpStatus.TOO_MANY_REQUESTS.value(), message);
            return false;
        }
    }

    abstract Bucket getBucket(String ip, String uri);
}

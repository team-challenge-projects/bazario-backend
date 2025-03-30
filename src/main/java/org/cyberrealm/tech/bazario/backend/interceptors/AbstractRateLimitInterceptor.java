package org.cyberrealm.tech.bazario.backend.interceptors;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.exception.ErrorResponse;
import org.cyberrealm.tech.bazario.backend.security.BucketBuilderService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public abstract class AbstractRateLimitInterceptor implements HandlerInterceptor {
    public static final int NUM_TOKENS_OF_PROBE = 1;
    public static final int DIVISOR_TO_SECONDS = 1_000_000_000;
    private final ObjectMapper mapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        var ip = request.getRemoteAddr();
        if (ip == null || ip.isBlank()) {return false;}
        var bucket = getBucket(ip);
        var probe = bucket.tryConsumeAndReturnRemaining(NUM_TOKENS_OF_PROBE);
        if (probe.isConsumed()) {
            response.addHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
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

    abstract Bucket getBucket(String ip);
}

package org.cyberrealm.tech.bazario.backend.interceptors;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.Bucket;
import org.cyberrealm.tech.bazario.backend.security.BucketBuilderService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LimitedSecurityRateLimitInterceptor extends AbstractRateLimitInterceptor {

    private static final String PREFIX_LIMITED = "LimitedSecurity_";

    @Value("${bucket.limited-security.tokens}")
    private int tokens;
    @Value("${bucket.limited-security.duration}")
    private int duration;
    private final BucketBuilderService builderService;

    public LimitedSecurityRateLimitInterceptor(ObjectMapper mapper,
                                               BucketBuilderService builderService) {
        super(mapper);
        this.builderService = builderService;
    }

    @Override
    Bucket getBucket(String ip) {
        return builderService.resolveBucket(PREFIX_LIMITED + ip, tokens, duration);
    }
}

package org.cyberrealm.tech.bazario.backend.interceptors;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.Bucket;
import org.cyberrealm.tech.bazario.backend.security.BucketBuilderService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GlobalRateLimitInterceptor extends AbstractRateLimitInterceptor {

    private static final String PREFIX_GLOBAL = "Global_";

    @Value("${bucket.global.tokens}")
    private int tokens;
    @Value("${bucket.global.duration}")
    private int duration;
    private final BucketBuilderService builderService;

    public GlobalRateLimitInterceptor(ObjectMapper mapper, BucketBuilderService builderService) {
        super(mapper);
        this.builderService = builderService;
    }

    @Override
    protected Bucket getBucket(String ip, String uri) {
        return builderService.resolveBucket(PREFIX_GLOBAL + ip, tokens, duration);
    }
}

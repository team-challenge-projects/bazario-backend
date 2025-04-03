package org.cyberrealm.tech.bazario.backend.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class BucketBuilderService {
    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    public Bucket resolveBucket(String ip, int tokens, int duration) {
        return cache.computeIfAbsent(ip, key -> newBucket(tokens, duration));
    }

    private Bucket newBucket(int tokens, int duration) {
        var limit = Bandwidth.builder()
                .capacity(tokens)
                .refillGreedy(tokens, Duration.ofSeconds(duration))
                .build();
        return Bucket.builder().addLimit(limit).build();
    }
}

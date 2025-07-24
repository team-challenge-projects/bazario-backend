package org.cyberrealm.tech.bazario.backend.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.dto.BucketDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BucketBuilderService {
    private final Map<String, BucketDto> cache = new ConcurrentHashMap<>();

    @Value("${bucket.storage-period}")
    private int storagePeriod;

    /**
     * Saving the bucket to the cache and deleting it from the cache
     * if it has expired.
     *
     * @param ip Key contains ip address users
     * @param tokens Number of tokens for probe
     * @param duration Probes update time
     * @return Bucket by key from cache
     */
    public Bucket resolveBucket(String ip, int tokens, int duration) {
        long currentTime = Instant.now().toEpochMilli();
        cache.entrySet().removeIf(entry ->
                entry.getValue().time() < currentTime);
        return cache.computeIfAbsent(ip, key -> new BucketDto(newBucket(tokens, duration),
                currentTime + Duration.ofMinutes(storagePeriod).toMillis())).bucket();

    }

    /**
     * Create new bucket
     *
     * @param tokens Number of tokens for probe
     * @param duration Probes update time
     * @return New bucket
     */
    private Bucket newBucket(int tokens, int duration) {
        var limit = Bandwidth.builder()
                .capacity(tokens)
                .refillGreedy(tokens, Duration.ofSeconds(duration))
                .build();
        return Bucket.builder().addLimit(limit).build();
    }
}

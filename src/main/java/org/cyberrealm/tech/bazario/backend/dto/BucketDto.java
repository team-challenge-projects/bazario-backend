package org.cyberrealm.tech.bazario.backend.dto;

import io.github.bucket4j.Bucket;

public record BucketDto(
        Bucket bucket, long time
) {
}

package org.cyberrealm.tech.bazario.backend.dto.ad;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

public record CreateAdRequestDto(
        String title,
        String imageUrl,
        BigDecimal price,
        Integer rating,
        LocalDate publicationDate,
        Set<Long> categoryIds
) {
}

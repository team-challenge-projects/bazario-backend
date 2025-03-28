package org.cyberrealm.tech.bazario.backend.dto.ad;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateAdRequestDto(
        String title,
        BigDecimal price,
        LocalDate publicationDate
) {
}

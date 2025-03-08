package org.cyberrealm.tech.bazario.backend.dto.ad;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdDto {
    private Long id;
    private String title;
    private String imageUrl;
    private BigDecimal price;
    private Integer rating;
    private LocalDate publicationDate;
    private Set<Long> categoryIds = new HashSet<>();
}

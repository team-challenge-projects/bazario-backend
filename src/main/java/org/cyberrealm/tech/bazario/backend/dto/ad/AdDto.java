package org.cyberrealm.tech.bazario.backend.dto.ad;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdDto {
    private Long id;
    private String title;
    private BigDecimal price;
    private LocalDate publicationDate;
}

package org.cyberrealm.tech.bazario.backend.dto.script;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import org.cyberrealm.tech.bazario.backend.dto.AdStatus;

@Getter
@Setter
public class AdCredentials {
    private String title;
    private String description;
    private BigDecimal price;
    private AdStatus status;
    private int user;
    private int category;
    private String cityName;
    private String cityCoordinate;
}

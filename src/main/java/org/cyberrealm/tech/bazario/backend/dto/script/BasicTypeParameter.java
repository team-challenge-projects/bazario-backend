package org.cyberrealm.tech.bazario.backend.dto.script;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BasicTypeParameter {
    private String name;
    private String restrictionPattern;
    private String descriptionPattern;
}

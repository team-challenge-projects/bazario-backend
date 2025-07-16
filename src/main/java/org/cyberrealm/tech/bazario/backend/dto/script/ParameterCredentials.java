package org.cyberrealm.tech.bazario.backend.dto.script;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParameterCredentials {
    private int ownerId;
    private String parameterValue;
    private int typeParameter;
}

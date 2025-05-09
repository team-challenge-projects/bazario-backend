package org.cyberrealm.tech.bazario.backend.dto.script;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryCredentials {
    private String name;
    private String image;
    private List<Integer> typeAdParameters;
}

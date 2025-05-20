package org.cyberrealm.tech.bazario.backend.scripts.service;

import java.util.List;
import org.cyberrealm.tech.bazario.backend.dto.script.BasicTypeParameter;
import org.cyberrealm.tech.bazario.backend.model.TypeAdParameter;

public interface AdTypeInitializer {
    TypeAdParameter getAdType(BasicTypeParameter parameter);

    List<TypeAdParameter> getAdTypes(List<BasicTypeParameter> parameters);
}

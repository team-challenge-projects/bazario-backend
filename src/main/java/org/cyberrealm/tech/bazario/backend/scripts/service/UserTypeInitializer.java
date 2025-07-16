package org.cyberrealm.tech.bazario.backend.scripts.service;

import java.util.List;
import org.cyberrealm.tech.bazario.backend.dto.script.BasicTypeParameter;
import org.cyberrealm.tech.bazario.backend.model.TypeUserParameter;

public interface UserTypeInitializer {
    List<TypeUserParameter> getUserType(List<BasicTypeParameter> parameters);
}

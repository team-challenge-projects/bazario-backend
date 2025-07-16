package org.cyberrealm.tech.bazario.backend.scripts.service;

import java.util.List;
import org.cyberrealm.tech.bazario.backend.dto.script.ParameterCredentials;
import org.cyberrealm.tech.bazario.backend.model.TypeUserParameter;
import org.cyberrealm.tech.bazario.backend.model.User;

public interface UserParameterInitializer {
    void addParameters(List<ParameterCredentials> parameters, List<User> users,
                       List<TypeUserParameter> types);
}

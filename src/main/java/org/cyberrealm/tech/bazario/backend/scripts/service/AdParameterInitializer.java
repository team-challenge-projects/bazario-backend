package org.cyberrealm.tech.bazario.backend.scripts.service;

import java.util.List;
import org.cyberrealm.tech.bazario.backend.dto.script.ParameterCredentials;
import org.cyberrealm.tech.bazario.backend.model.Ad;
import org.cyberrealm.tech.bazario.backend.model.TypeAdParameter;

public interface AdParameterInitializer {
    void addParameters(List<ParameterCredentials> credentials,
                       List<Ad> ads, List<TypeAdParameter> adTypes);
}

package org.cyberrealm.tech.bazario.backend.scripts.service;

import java.util.List;
import org.cyberrealm.tech.bazario.backend.dto.script.ParameterItemCredentials;
import org.cyberrealm.tech.bazario.backend.model.Ad;
import org.cyberrealm.tech.bazario.backend.model.CategoryTypeAdParameter;

public interface AdParameterInitializer {
    void addParameters(List<ParameterItemCredentials> credentials,
                       List<Ad> ads, List<CategoryTypeAdParameter> parameters);
}

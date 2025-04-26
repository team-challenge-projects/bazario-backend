package org.cyberrealm.tech.bazario.backend.service;

import java.util.List;
import org.cyberrealm.tech.bazario.backend.dto.BasicAdminParameter;
import org.cyberrealm.tech.bazario.backend.dto.BasicUserParameter;

public interface TypeAdParameterService {
    Long create(BasicAdminParameter parameter);

    void update(Long id, BasicAdminParameter parameter);

    void delete(Long id);

    void checkParameters(List<BasicUserParameter> adParameters);
}

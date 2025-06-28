package org.cyberrealm.tech.bazario.backend.service;

import org.cyberrealm.tech.bazario.backend.dto.BasicAdminParameter;
import org.cyberrealm.tech.bazario.backend.dto.BasicAdminParameterResponse;

public interface TypeUserParameterService {
    Long create(BasicAdminParameter parameter);

    void delete(Long id);

    BasicAdminParameterResponse update(Long id, BasicAdminParameter parameter);
}

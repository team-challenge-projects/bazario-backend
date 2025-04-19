package org.cyberrealm.tech.bazario.backend.service;

import org.cyberrealm.tech.bazario.backend.dto.BasicAdminParameter;

public interface TypeUserParameterService {
    Long create(BasicAdminParameter parameter);

    void delete(Long id);

    void update(Long id, BasicAdminParameter parameter);
}

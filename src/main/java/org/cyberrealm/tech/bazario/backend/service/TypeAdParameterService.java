package org.cyberrealm.tech.bazario.backend.service;

import org.cyberrealm.tech.bazario.backend.dto.BasicAdminParameter;

public interface TypeAdParameterService {
    Long create(BasicAdminParameter parameter);

    void update(Long id, BasicAdminParameter parameter);

    void delete(Long id);
}

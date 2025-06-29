package org.cyberrealm.tech.bazario.backend.service;

import org.cyberrealm.tech.bazario.backend.dto.BasicAdminParameter;
import org.cyberrealm.tech.bazario.backend.dto.BasicAdminParameterResponse;

public interface TypeUserParameterService {
    /**
     * Create type user parameter.
     *
     * @author Andrey Sitarskiy
     * @param parameter Dto for create type ad parameter
     * @return Type user parameter id
     */
    Long create(BasicAdminParameter parameter);

    /**
     * Delete type user parameter.
     *
     * @author Andrey Sitarskiy
     * @param id Type user parameter id
     */
    void delete(Long id);

    /**
     * Update type user parameter.
     *
     * @author Andrey Sitarskiy
     * @param id Type user parameter id
     * @param parameter Dto for update type user parameter
     * @return Dto of type user parameter
     */
    BasicAdminParameterResponse update(Long id, BasicAdminParameter parameter);
}

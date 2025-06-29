package org.cyberrealm.tech.bazario.backend.service;

import java.util.List;
import java.util.Map;
import org.cyberrealm.tech.bazario.backend.dto.BasicAdminParameter;
import org.cyberrealm.tech.bazario.backend.dto.BasicAdminParameterResponse;
import org.cyberrealm.tech.bazario.backend.dto.BasicUserParameter;
import org.springframework.data.domain.Page;

public interface TypeAdParameterService {
    /**
     * Get page with dto of type ad parameter by filter.
     * Filter contains pagination
     *
     * @author Andrey Sitarskiy
     * @param filters Name parameter and value
     * @return Page dto of type ad parameter
     */
    Page<BasicAdminParameterResponse> getAll(Map<String, String> filters);

    /**
     * Create type ad parameter.
     *
     * @author Andrey Sitarskiy
     * @param parameter Dto for create type ad parameter
     * @return Type ad parameter id
     */
    Long create(BasicAdminParameter parameter);

    /**
     * Update type ad parameter.
     *
     * @author Andrey Sitarskiy
     * @param id Type ad parameter id
     * @param parameter Dto for update type ad parameter
     * @return Dto of type ad parameter
     */
    BasicAdminParameterResponse update(Long id, BasicAdminParameter parameter);

    /**
     * Delete type ad parameter.
     *
     * @author Andrey Sitarskiy
     * @param id Type ad parameter id
     */
    void delete(Long id);

    /**
     * Check parameter of dto by restriction pattern from
     * type ad parameter
     *
     * @author Andrey Sitarskiy
     * @param adParameters Ad parameter from dto
     */
    void checkParameters(List<BasicUserParameter> adParameters);
}

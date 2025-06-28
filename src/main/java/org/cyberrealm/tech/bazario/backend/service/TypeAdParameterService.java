package org.cyberrealm.tech.bazario.backend.service;

import java.util.List;
import java.util.Map;
import org.cyberrealm.tech.bazario.backend.dto.BasicAdminParameter;
import org.cyberrealm.tech.bazario.backend.dto.BasicAdminParameterResponse;
import org.cyberrealm.tech.bazario.backend.dto.BasicUserParameter;
import org.springframework.data.domain.Page;

public interface TypeAdParameterService {
    Page<BasicAdminParameterResponse> getAll(Map<String, String> filters);

    Long create(BasicAdminParameter parameter);

    BasicAdminParameterResponse update(Long id, BasicAdminParameter parameter);

    void delete(Long id);

    void checkParameters(List<BasicUserParameter> adParameters);
}

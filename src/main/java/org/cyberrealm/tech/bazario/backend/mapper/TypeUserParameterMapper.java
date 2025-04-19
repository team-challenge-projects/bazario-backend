package org.cyberrealm.tech.bazario.backend.mapper;

import org.cyberrealm.tech.bazario.backend.config.MapperConfig;
import org.cyberrealm.tech.bazario.backend.dto.BasicAdminParameter;
import org.cyberrealm.tech.bazario.backend.dto.BasicAdminParameterResponse;
import org.cyberrealm.tech.bazario.backend.model.TypeUserParameter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface TypeUserParameterMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "categories", ignore = true)
    TypeUserParameter toTypeUserParameter(BasicAdminParameter parameter);

    @Mapping(target = "categories", ignore = true)
    TypeUserParameter toTypeUserParameter(Long id, BasicAdminParameter parameter);

    BasicAdminParameterResponse toBasicAdminParameter(TypeUserParameter parameter);
}

package org.cyberrealm.tech.bazario.backend.mapper;

import org.cyberrealm.tech.bazario.backend.config.MapperConfig;
import org.cyberrealm.tech.bazario.backend.dto.BasicAdminParameter;
import org.cyberrealm.tech.bazario.backend.dto.BasicAdminParameterCategory;
import org.cyberrealm.tech.bazario.backend.dto.BasicAdminParameterResponse;
import org.cyberrealm.tech.bazario.backend.dto.script.BasicTypeParameter;
import org.cyberrealm.tech.bazario.backend.model.TypeAdParameter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface TypeAdParameterMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "parameters", ignore = true)
    TypeAdParameter toTypeAdParameter(BasicAdminParameter parameter);

    @Mapping(target = "parameters", ignore = true)
    TypeAdParameter toTypeAdParameter(Long id, BasicAdminParameter parameter);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "parameters", ignore = true)
    TypeAdParameter toTypeAdParameter(BasicTypeParameter parameter);

    BasicAdminParameterResponse toBasicAdminParameter(TypeAdParameter parameter);

    @Mapping(target = "values", ignore = true)
    BasicAdminParameterCategory toBasicAdminParameterCategory(TypeAdParameter parameter);
}

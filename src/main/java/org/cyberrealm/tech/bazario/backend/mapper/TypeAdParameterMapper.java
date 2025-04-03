package org.cyberrealm.tech.bazario.backend.mapper;

import org.cyberrealm.tech.bazario.backend.config.MapperConfig;
import org.cyberrealm.tech.bazario.backend.dto.BasicAdminParameter;
import org.cyberrealm.tech.bazario.backend.model.TypeAdParameter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface TypeAdParameterMapper {
    TypeAdParameter toTypeAdParameter(BasicAdminParameter parameter);

    @Mapping(target = "id", source = "id")
    TypeAdParameter toTypeAdParameter(Long id, BasicAdminParameter parameter);
}

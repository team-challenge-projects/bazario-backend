package org.cyberrealm.tech.bazario.backend.mapper;

import org.cyberrealm.tech.bazario.backend.config.MapperConfig;
import org.mapstruct.Mapper;
import org.openapitools.jackson.nullable.JsonNullable;

@Mapper(config = MapperConfig.class)
public interface JsonNullableMapper {
    String map(JsonNullable<String> source);
}

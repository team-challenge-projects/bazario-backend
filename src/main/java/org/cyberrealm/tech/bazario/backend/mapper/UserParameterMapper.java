package org.cyberrealm.tech.bazario.backend.mapper;

import org.cyberrealm.tech.bazario.backend.config.MapperConfig;
import org.cyberrealm.tech.bazario.backend.dto.BasicParameter;
import org.cyberrealm.tech.bazario.backend.dto.BasicUserParameter;
import org.cyberrealm.tech.bazario.backend.model.TypeUserParameter;
import org.cyberrealm.tech.bazario.backend.model.UserParameter;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = MapperConfig.class)
public interface UserParameterMapper {
    @Mapping(target = "typeId", source = "parameter.id")
    @Mapping(target = "typeName", source = "parameter.name")
    @Mapping(target = "descriptionPattern", source = "parameter.descriptionPattern")
    @Mapping(target = "typeView", source = "parameter.typeView")
    BasicParameter toBasicParameter(UserParameter userParameter);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "parameter", source = "typeId", qualifiedByName = "mapTypeUserParameter")
    @Mapping(target = "id", ignore = true)
    UserParameter toUserParameter(BasicUserParameter basicParameter);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "parameter", source = "typeId", qualifiedByName = "mapTypeUserParameter")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserParameter(BasicUserParameter dto, @MappingTarget UserParameter parameter);

    @Named("mapTypeUserParameter")
    default TypeUserParameter mapTypeUserParameter(Long id) {
        if (id == null) {
            return null;
        }
        TypeUserParameter type = new TypeUserParameter();
        type.setId(id);
        return type;
    }
}

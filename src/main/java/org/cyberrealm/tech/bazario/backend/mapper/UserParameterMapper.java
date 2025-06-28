package org.cyberrealm.tech.bazario.backend.mapper;

import org.cyberrealm.tech.bazario.backend.config.MapperConfig;
import org.cyberrealm.tech.bazario.backend.dto.BasicParameter;
import org.cyberrealm.tech.bazario.backend.dto.BasicUserParameter;
import org.cyberrealm.tech.bazario.backend.model.UserParameter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(config = MapperConfig.class)
public interface UserParameterMapper {
    UserParameterMapper INSTANCE = Mappers.getMapper(UserParameterMapper.class);

    @Mapping(target = "typeId", source = "parameter.id")
    @Mapping(target = "typeName", source = "parameter.name")
    @Mapping(target = "restrictionPattern", source = "parameter.restrictionPattern")
    @Mapping(target = "descriptionPattern", source = "parameter.descriptionPattern")
    BasicParameter toBasicParameter(UserParameter userParameter);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "parameter.id", source = "typeId")
    UserParameter toUserParameter(BasicUserParameter basicParameter);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "parameter.id", source = "typeId")
    void updateUserParameter(BasicUserParameter dto, @MappingTarget UserParameter parameter);
}

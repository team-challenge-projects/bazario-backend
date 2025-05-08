package org.cyberrealm.tech.bazario.backend.mapper;

import org.cyberrealm.tech.bazario.backend.config.MapperConfig;
import org.cyberrealm.tech.bazario.backend.config.RootUserCredentials;
import org.cyberrealm.tech.bazario.backend.dto.RegistrationRequest;
import org.cyberrealm.tech.bazario.backend.dto.UserResponseDto;
import org.cyberrealm.tech.bazario.backend.dto.script.UserCredentials;
import org.cyberrealm.tech.bazario.backend.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class, uses = {JsonNullableMapper.class})
public interface UserMapper {
    UserResponseDto toUserResponse(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lastName", ignore = true)
    @Mapping(target = "avatar", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "cityName", ignore = true)
    @Mapping(target = "cityCoordinate", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "locked", ignore = true)
    @Mapping(target = "parameters", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    User toModel(RegistrationRequest requestDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "locked", ignore = true)
    @Mapping(target = "parameters", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    User toUser(RootUserCredentials credentials);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "locked", ignore = true)
    @Mapping(target = "parameters", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    User toUser(UserCredentials credentials);
}

package org.cyberrealm.tech.bazario.backend.mapper;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.cyberrealm.tech.bazario.backend.config.MapperConfig;
import org.cyberrealm.tech.bazario.backend.config.RootUserCredentials;
import org.cyberrealm.tech.bazario.backend.dto.BasicUserParameter;
import org.cyberrealm.tech.bazario.backend.dto.PatchUser;
import org.cyberrealm.tech.bazario.backend.dto.PrivateUserInformation;
import org.cyberrealm.tech.bazario.backend.dto.PublicUserInformation;
import org.cyberrealm.tech.bazario.backend.dto.RegistrationRequest;
import org.cyberrealm.tech.bazario.backend.dto.UserInformation;
import org.cyberrealm.tech.bazario.backend.dto.UserResponseDto;
import org.cyberrealm.tech.bazario.backend.dto.script.UserCredentials;
import org.cyberrealm.tech.bazario.backend.model.User;
import org.cyberrealm.tech.bazario.backend.model.UserParameter;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = MapperConfig.class, uses = {JsonNullableMapper.class,
        UserParameterMapper.class})
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

    PrivateUserInformation toInformation(User currentUser);

    PublicUserInformation toInformationForAnonymous(User user);

    @Mapping(target = "distance", source = "distance")
    UserInformation toPublicInformation(User user, double distance);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "avatar", ignore = true)
    @Mapping(target = "locked", source = "isLocked")
    @Mapping(target = "parameters", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "email", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUser(PatchUser patchUser, @MappingTarget User user);

    @AfterMapping
    default void updateOrAddUserParameters(PatchUser patchUser, @MappingTarget User currentUser) {
        var dtoParameters = patchUser.getUserParameters();
        var parameters = currentUser.getParameters();

        if (dtoParameters == null) {
            return;
        }

        if (parameters == null) {
            currentUser.setParameters(dtoParameters.stream().map(dto -> {
                var userParam = UserParameterMapper.INSTANCE.toUserParameter(dto);
                userParam.setUser(currentUser);
                return userParam;
            }).collect(Collectors.toSet()));
            return;
        }
        Set<UserParameter> toRemove = new HashSet<>(parameters);
        for (BasicUserParameter dto : dtoParameters) {
            boolean found = false;
            for (UserParameter param : parameters) {
                if (Objects.equals(dto.getId(), param.getId())) {
                    UserParameterMapper.INSTANCE.updateUserParameter(dto, param);
                    toRemove.remove(param);
                    found = true;
                    break;
                }
            }
            if (!found) {
                var userParam = UserParameterMapper.INSTANCE.toUserParameter(dto);
                userParam.setUser(currentUser);
                parameters.add(userParam);
            }
        }
        parameters.removeAll(toRemove);
    }
}

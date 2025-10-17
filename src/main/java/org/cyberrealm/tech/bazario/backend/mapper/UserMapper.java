package org.cyberrealm.tech.bazario.backend.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.cyberrealm.tech.bazario.backend.config.MapperConfig;
import org.cyberrealm.tech.bazario.backend.config.RootUserCredentials;
import org.cyberrealm.tech.bazario.backend.dto.BasicUserParameter;
import org.cyberrealm.tech.bazario.backend.dto.PatchUser;
import org.cyberrealm.tech.bazario.backend.dto.PrivateUserInformation;
import org.cyberrealm.tech.bazario.backend.dto.PublicUserInformation;
import org.cyberrealm.tech.bazario.backend.dto.RegistrationRequest;
import org.cyberrealm.tech.bazario.backend.dto.RegistrationResponse;
import org.cyberrealm.tech.bazario.backend.dto.UserInformation;
import org.cyberrealm.tech.bazario.backend.dto.UserResponseDto;
import org.cyberrealm.tech.bazario.backend.dto.script.UserCredentials;
import org.cyberrealm.tech.bazario.backend.model.TypeUserParameter;
import org.cyberrealm.tech.bazario.backend.model.User;
import org.cyberrealm.tech.bazario.backend.model.UserParameter;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(config = MapperConfig.class, uses = {UserParameterMapper.class})
public abstract class UserMapper {
    @Autowired
    protected UserParameterMapper userParameterMapper;

    public abstract UserResponseDto toUserResponse(User user);

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
    public abstract User toModel(RegistrationRequest requestDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "locked", ignore = true)
    @Mapping(target = "parameters", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "cityCoordinate", ignore = true)
    public abstract User toUser(RootUserCredentials credentials);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "locked", ignore = true)
    @Mapping(target = "parameters", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "cityCoordinate", ignore = true)
    public abstract User toUser(UserCredentials credentials);

    @Mapping(target = "cityCoordinate", expression =
            "java(currentUser.getCityCoordinate() != null "
                    + "? currentUser.getCityCoordinate().toText() : \"\")")
    public abstract PrivateUserInformation toInformation(User currentUser);

    public abstract PublicUserInformation toInformationForAnonymous(User user);

    @Mapping(target = "distance", source = "distance")
    public abstract UserInformation toPublicInformation(User user, double distance);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "avatar", ignore = true)
    @Mapping(target = "locked", source = "isLocked")
    @Mapping(target = "parameters", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "cityCoordinate", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void updateUser(PatchUser patchUser, @MappingTarget User user);

    @AfterMapping
    public void updateOrAddUserParameters(PatchUser patchUser, @MappingTarget User currentUser) {
        var dtoParameters = patchUser.getUserParameters();
        var parameters = currentUser.getParameters();

        if (dtoParameters == null) {
            return;
        }

        if (parameters == null) {
            currentUser.setParameters(dtoParameters.stream().map(dto -> {
                var userParam = userParameterMapper.toUserParameter(dto);
                userParam.setUser(currentUser);
                return userParam;
            }).collect(Collectors.toSet()));
            return;
        }
        List<UserParameter> toRemove = new ArrayList<>(parameters);
        List<BasicUserParameter> toUpdate = new ArrayList<>(dtoParameters);
        for (BasicUserParameter dto : dtoParameters) {
            boolean found = false;
            for (UserParameter param : parameters) {
                if (Objects.equals(dto.getTypeId(), param.getParameter().getId())) {
                    if (dto.getParameterValue() != null && param.getParameterValue() != null
                            && !dto.getParameterValue().equals(param.getParameterValue())) {
                        userParameterMapper.updateUserParameter(dto, param);
                    }
                    toRemove.remove(param);
                    toUpdate.remove(dto);
                }
            }
        }
        if (!toUpdate.isEmpty()) {
            var sizeRemove = toRemove.size();
            for (int i = 0; i < toUpdate.size(); i++) {
                if (i < sizeRemove) {
                    var typeParameter = new TypeUserParameter();
                    typeParameter.setId(toUpdate.get(i).getTypeId());
                    UserParameter userParameter = toRemove.remove(i);
                    userParameter.setParameter(typeParameter);
                    userParameter.setParameterValue(
                            toUpdate.get(i).getParameterValue());
                } else {
                    var userParameter = new UserParameter();
                    userParameter.setUser(currentUser);
                    var typeParameter = new TypeUserParameter();
                    typeParameter.setId(toUpdate.get(i).getTypeId());
                    userParameter.setParameter(typeParameter);
                    userParameter.setParameterValue(
                            toUpdate.get(i).getParameterValue());
                    parameters.add(userParameter);
                }
            }
        }
        if (!toRemove.isEmpty()) {
            toRemove.forEach(parameters::remove);
        }
    }

    public abstract RegistrationResponse toRegistrationResponse(RegistrationRequest requestDto);
}

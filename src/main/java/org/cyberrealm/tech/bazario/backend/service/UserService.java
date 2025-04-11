package org.cyberrealm.tech.bazario.backend.service;

import org.cyberrealm.tech.bazario.backend.dto.UserRegistrationRequestDto;
import org.cyberrealm.tech.bazario.backend.dto.UserResponseDto;
import org.cyberrealm.tech.bazario.backend.exception.custom.RegistrationException;
import org.cyberrealm.tech.bazario.backend.model.User;

import java.util.Map;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto requestDto) throws RegistrationException;

    User getCurrentUser();

    void save(User user);

    User getUserById(Long id);
}

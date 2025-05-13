package org.cyberrealm.tech.bazario.backend.service;

import org.cyberrealm.tech.bazario.backend.dto.RegistrationRequest;
import org.cyberrealm.tech.bazario.backend.exception.custom.RegistrationException;
import org.cyberrealm.tech.bazario.backend.model.User;

public interface UserService {
    void register(RegistrationRequest requestDto) throws RegistrationException;

    void save(User user);

    User getUserById(Long id);
}

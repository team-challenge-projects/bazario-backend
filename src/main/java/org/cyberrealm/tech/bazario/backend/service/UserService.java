package org.cyberrealm.tech.bazario.backend.service;

import org.cyberrealm.tech.bazario.backend.dto.PatchUser;
import org.cyberrealm.tech.bazario.backend.dto.PrivateUserInformation;
import org.cyberrealm.tech.bazario.backend.dto.RegistrationRequest;
import org.cyberrealm.tech.bazario.backend.dto.UserInformation;
import org.cyberrealm.tech.bazario.backend.exception.custom.RegistrationException;
import org.cyberrealm.tech.bazario.backend.model.User;

public interface UserService {
    void register(RegistrationRequest requestDto) throws RegistrationException;

    void save(User user);

    User getUserById(Long id);

    PrivateUserInformation getInformation();

    UserInformation getInformationById(Long id);

    PrivateUserInformation update(PatchUser patchUser);

    PrivateUserInformation updateById(Long id, PatchUser patchUser);

    void delete();

    void deleteById(Long id);
}

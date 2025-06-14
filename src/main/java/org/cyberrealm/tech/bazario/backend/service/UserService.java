package org.cyberrealm.tech.bazario.backend.service;

import org.cyberrealm.tech.bazario.backend.dto.PatchUser;
import org.cyberrealm.tech.bazario.backend.dto.PrivateUserInformation;
import org.cyberrealm.tech.bazario.backend.dto.PublicUserInformation;
import org.cyberrealm.tech.bazario.backend.dto.RegistrationRequest;
import org.cyberrealm.tech.bazario.backend.dto.UserInformation;
import org.cyberrealm.tech.bazario.backend.exception.custom.RegistrationException;

public interface UserService {
    void register(RegistrationRequest requestDto) throws RegistrationException;

    PrivateUserInformation getInformation();

    UserInformation getInformationById(Long id);

    PrivateUserInformation update(PatchUser patchUser);

    PrivateUserInformation updateById(Long id, PatchUser patchUser);

    void delete();

    void deleteById(Long id);

    PublicUserInformation getPublicInformationById(Long id);
}

package org.cyberrealm.tech.bazario.backend.service;

import java.util.List;
import org.cyberrealm.tech.bazario.backend.dto.PatchUser;
import org.cyberrealm.tech.bazario.backend.dto.PrivateUserInformation;
import org.cyberrealm.tech.bazario.backend.dto.PublicUserInformation;
import org.cyberrealm.tech.bazario.backend.dto.RegistrationRequest;
import org.cyberrealm.tech.bazario.backend.dto.UserInformation;
import org.cyberrealm.tech.bazario.backend.exception.custom.RegistrationException;

public interface UserService {
    /**
     * Filling out the registration form and saving it to the cache
     *
     * @author Andrey Sitarskiy
     * @param requestDto Registration form
     */
    void register(RegistrationRequest requestDto) throws RegistrationException;

    /**
     * Get full information about user
     *
     * @author Andrey Sitarskiy
     * @return Full information about user
     */
    PrivateUserInformation getInformation();

    /**
     * Get information for authorization user
     *
     * @author Andrey Sitarskiy
     * @param id User id
     * @return Information about user
     */
    UserInformation getInformationById(Long id);

    /**
     * Update user
     *
     * @author Andrey Sitarskiy
     * @param patchUser Dto for update user
     * @return Full information about user
     */
    PrivateUserInformation update(PatchUser patchUser);

    /**
     * Update user by admin
     *
     * @author Andrey Sitarskiy
     * @param id User id
     * @param patchUser Dto for update user
     * @return Full information about user
     */
    PrivateUserInformation updateById(Long id, PatchUser patchUser);

    /**
     * Deleting the user by adding a prefix to email, blocking a user,
     * changing the status of announcements to DELETE
     *
     * @author Andrey Sitarskiy
     */
    void delete();

    /**
     * Deleting the user by user id
     *
     * @author Andrey Sitarskiy
     * @param id User id
     */
    void deleteById(Long id);

    /**
     * Get public information about user
     *
     * @author Andrey Sitarskiy
     * @param id User id
     * @return Public information about user
     */
    PublicUserInformation getPublicInformationById(Long id);

    /**
     * Get user ids by distance
     *
     * @author Andrey Sitarskiy
     * @param distance Distance between the user's starting coordinates
     * @return Ids of find user
     */
    List<Long> getUserIdByDistance(double distance);
}

package org.cyberrealm.tech.bazario.backend.service;

import org.cyberrealm.tech.bazario.backend.exception.custom.AuthenticationException;
import org.cyberrealm.tech.bazario.backend.model.User;

public interface AuthenticationUserService {
    /**
     * Get authorization user
     *
     * @author Andrey Sitarskiy
     * @return Authorization user
     * @exception AuthenticationException If user is not authorization
     */
    User getCurrentUser();

    /**
     * Check authorization user
     *
     * @author Andrey Sitarskiy
     * @return user is authorization
     */
    boolean isAuthenticationUser();

    /**
     * Check role ADMIN by authorization user
     *
     * @author Andrey Sitarskiy
     * @return user is authorization
     */
    boolean isAdmin();

    /**
     * Check role ROOT by authorization user
     *
     * @author Andrey Sitarskiy
     * @return user is authorization
     */
    boolean isRoot();
}

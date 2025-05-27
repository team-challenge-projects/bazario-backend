package org.cyberrealm.tech.bazario.backend.service;

import org.cyberrealm.tech.bazario.backend.model.User;

public interface AuthenticationUserService {
    User getCurrentUser();

    boolean isAuthenticationUser();

    boolean isAdmin();

    boolean isRoot();
}

package org.cyberrealm.tech.bazario.backend.service;

import org.cyberrealm.tech.bazario.backend.model.Ad;
import org.cyberrealm.tech.bazario.backend.model.User;

public interface AccessAdService {
    Ad getProtectedAd(Long id);

    void save(Ad ad);

    boolean isNotAccessAd(Ad ad);

    User getUser();

    Ad getPublicAd(Long id);

    boolean isAuthenticationUser();

    boolean isAdmin();
}

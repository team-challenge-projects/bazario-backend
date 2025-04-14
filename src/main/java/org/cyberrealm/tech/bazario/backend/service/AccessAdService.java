package org.cyberrealm.tech.bazario.backend.service;

import org.cyberrealm.tech.bazario.backend.model.Ad;

public interface AccessAdService {
    Ad getAd(Long id);

    void save(Ad ad);

    boolean isNotAccessAd(Ad ad);
}

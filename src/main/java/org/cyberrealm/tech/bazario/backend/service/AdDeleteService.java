package org.cyberrealm.tech.bazario.backend.service;

import org.cyberrealm.tech.bazario.backend.dto.AdStatus;
import org.cyberrealm.tech.bazario.backend.model.User;

public interface AdDeleteService {
    void changeStatusByUser(User user, AdStatus status);

    void deleteByUser(User user);
}

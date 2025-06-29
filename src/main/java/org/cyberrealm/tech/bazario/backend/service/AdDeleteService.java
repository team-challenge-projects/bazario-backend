package org.cyberrealm.tech.bazario.backend.service;

import org.cyberrealm.tech.bazario.backend.dto.AdStatus;
import org.cyberrealm.tech.bazario.backend.model.User;

public interface AdDeleteService {
    /**
     * Changes the status of ads owned by the user
     *
     * @author Andrey Sitarskiy
     * @param user owner
     * @param status New ad status
     */
    void changeStatusByUser(User user, AdStatus status);

    /**
     * Delete ads owned by the user
     *
     * @author Andrey Sitarskiy
     * @param user owner
     */
    void deleteByUser(User user);
}

package org.cyberrealm.tech.bazario.backend.scripts.service;

import org.cyberrealm.tech.bazario.backend.dto.script.UserCredentials;
import org.cyberrealm.tech.bazario.backend.model.User;

public interface UserInitializer {
    User getUser(UserCredentials credentials);
}

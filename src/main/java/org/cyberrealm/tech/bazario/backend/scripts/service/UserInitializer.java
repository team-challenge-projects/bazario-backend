package org.cyberrealm.tech.bazario.backend.scripts.service;

import java.util.List;
import org.cyberrealm.tech.bazario.backend.dto.script.UserCredentials;
import org.cyberrealm.tech.bazario.backend.model.User;

public interface UserInitializer {
    User getUser(UserCredentials credentials);

    List<User> getUsers(List<UserCredentials> dtoUsers);
}

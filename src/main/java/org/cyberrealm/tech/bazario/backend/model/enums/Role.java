package org.cyberrealm.tech.bazario.backend.model.enums;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ROLE_ADMIN, ROLE_USER, ROLE_ROOT;

    @Override
    public String getAuthority() {
        return name();
    }
}

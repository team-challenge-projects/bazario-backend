package org.cyberrealm.tech.bazario.backend.model.enums;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ADMIN, USER, ROOT;

    @Override
    public String getAuthority() {
        return name();
    }
}

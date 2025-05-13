package org.cyberrealm.tech.bazario.backend.service.impl;

import java.util.NoSuchElementException;
import org.cyberrealm.tech.bazario.backend.exception.custom.AuthenticationException;
import org.cyberrealm.tech.bazario.backend.model.User;
import org.cyberrealm.tech.bazario.backend.model.enums.Role;
import org.cyberrealm.tech.bazario.backend.service.AuthenticationUserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationUserServiceImpl implements AuthenticationUserService {
    @Override
    public User getCurrentUser() {
        if (!isAuthenticationUser()) {
            throw new AuthenticationException("User not authenticated");
        }
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Override
    public boolean isAuthenticationUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        try {
            Role.valueOf(authentication.getAuthorities().stream()
                    .findFirst().orElseThrow().getAuthority());
        } catch (NoSuchElementException | IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        return authentication.getAuthorities().stream().anyMatch(role ->
                role.getAuthority().equals(Role.ADMIN.getAuthority())
                        || role.getAuthority().equals(Role.ROOT.getAuthority()));
    }
}

package org.cyberrealm.tech.bazario.backend.service.impl;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.dto.AdStatus;
import org.cyberrealm.tech.bazario.backend.exception.custom.AuthenticationException;
import org.cyberrealm.tech.bazario.backend.exception.custom.EntityNotFoundException;
import org.cyberrealm.tech.bazario.backend.exception.custom.ForbiddenException;
import org.cyberrealm.tech.bazario.backend.model.Ad;
import org.cyberrealm.tech.bazario.backend.model.User;
import org.cyberrealm.tech.bazario.backend.model.enums.Role;
import org.cyberrealm.tech.bazario.backend.repository.AdRepository;
import org.cyberrealm.tech.bazario.backend.service.AccessAdService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccessAdServiceImpl implements AccessAdService {
    private final AdRepository adRepository;
    private final Map<String, Ad> cache = new ConcurrentHashMap<>();

    @Override
    public Ad getProtectedAd(Long id) {
        var ad = getAdFromCache(id);
        if (isNotAccessAd(ad)) {
            throw new ForbiddenException("User not access to ad by id " + id);
        }
        return ad;
    }

    private Ad getAdFromCache(Long id) {
        String key = "AD_ID_" + id;
        cache.computeIfAbsent(key, k -> adRepository.findByIdWithParameters(id)
                .orElseThrow(() -> new EntityNotFoundException("Ad by id " + id
                        + " not found")));
        return cache.get(key);
    }

    @Override
    public void save(Ad ad) {
        adRepository.save(ad);
    }

    @Override
    public boolean isNotAccessAd(Ad ad) {
        if (!isAuthenticationUser()) {
            return true;
        }
        var user = getUser();
        return !(user.isAccountNonLocked() && (
                user.getRole().equals(Role.ROOT)
                        || user.getRole().equals(Role.ADMIN)
                        || (user.getRole().equals(Role.USER)
                        && ad.getUser().getId().equals(user.getId())
                        && !ad.getStatus().equals(AdStatus.DELETE))));
    }

    @Override
    public Ad getPublicAd(Long id) {
        var ad = getAdFromCache(id);
        if (!ad.getStatus().equals(AdStatus.ACTIVE) && isNotAccessAd(ad)) {
            throw new ForbiddenException("User not access to ad by id " + id);
        }
        return ad;
    }

    @Override
    public boolean isAuthenticationUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
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
        if (!isAuthenticationUser()) {
            return false;
        }
        var role = getUser().getRole();
        return role.equals(Role.ROOT) || role.equals(Role.ADMIN);
    }

    @Override
    public User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationException("User not authenticated");
        }

        return (User) authentication.getPrincipal();
    }
}

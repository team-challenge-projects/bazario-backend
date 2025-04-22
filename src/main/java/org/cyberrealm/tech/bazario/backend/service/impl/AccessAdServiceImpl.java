package org.cyberrealm.tech.bazario.backend.service.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
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
    public Ad getAd(Long id) {
        String key = "AD_ID_" + id;
        cache.computeIfAbsent(key, k -> adRepository.findByIdWithImages(id)
                .orElseThrow(() -> new EntityNotFoundException("Ad by id " + id
                        + " not found")));
        var ad = cache.get(key);
        if (isNotAccessAd(ad)) {
            throw new ForbiddenException("User not access to ad by id " + id);
        }
        return ad;
    }

    @Override
    public void save(Ad ad) {
        adRepository.save(ad);
    }

    @Override
    public boolean isNotAccessAd(Ad ad) {
        var user = getUser();
        return !(user.isAccountNonLocked() && (
                user.getRole().equals(Role.ROOT)
                        || user.getRole().equals(Role.ADMIN)
                        || (user.getRole().equals(Role.USER)
                        && ad.getUser().getId().equals(user.getId()))));
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

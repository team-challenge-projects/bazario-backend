package org.cyberrealm.tech.bazario.backend.service.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.dto.AdStatus;
import org.cyberrealm.tech.bazario.backend.exception.custom.EntityNotFoundException;
import org.cyberrealm.tech.bazario.backend.exception.custom.ForbiddenException;
import org.cyberrealm.tech.bazario.backend.model.Ad;
import org.cyberrealm.tech.bazario.backend.model.enums.Role;
import org.cyberrealm.tech.bazario.backend.repository.AdRepository;
import org.cyberrealm.tech.bazario.backend.service.AccessAdService;
import org.cyberrealm.tech.bazario.backend.service.AuthenticationUserService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccessAdServiceImpl implements AccessAdService {
    private final AdRepository adRepository;
    private final AuthenticationUserService authUserService;
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
        if (!authUserService.isAuthenticationUser()) {
            return true;
        }
        var user = authUserService.getCurrentUser();
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
}

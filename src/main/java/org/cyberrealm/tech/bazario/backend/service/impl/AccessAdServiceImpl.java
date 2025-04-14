package org.cyberrealm.tech.bazario.backend.service.impl;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.exception.custom.AuthenticationException;
import org.cyberrealm.tech.bazario.backend.exception.custom.EntityNotFoundException;
import org.cyberrealm.tech.bazario.backend.exception.custom.ForbiddenException;
import org.cyberrealm.tech.bazario.backend.model.Ad;
import org.cyberrealm.tech.bazario.backend.model.User;
import org.cyberrealm.tech.bazario.backend.model.enums.Role;
import org.cyberrealm.tech.bazario.backend.repository.AdRepository;
import org.cyberrealm.tech.bazario.backend.service.AccessAdService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccessAdServiceImpl implements AccessAdService {
    private final AdRepository adRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public Ad getAd(Long id) {
        String key = "AD_ID_" + id;
        redisTemplate.opsForValue().setIfAbsent(key, adRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Ad by id " + id
                                + " not found")),
                Duration.ofMinutes(15));
        var ad = (Ad) redisTemplate.opsForValue().get(key);
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationException("User not authenticated");
        }

        var user = (User) authentication.getPrincipal();
        return !(user.isAccountNonLocked() && (
                user.getRole().equals(Role.ROOT)
                        || user.getRole().equals(Role.ADMIN)
                        || (user.getRole().equals(Role.USER)
                        && ad.getUser().getId().equals(user.getId()))));
    }
}

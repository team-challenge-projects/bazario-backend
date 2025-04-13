package org.cyberrealm.tech.bazario.backend.service.impl;

import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.dto.AdDto;
import org.cyberrealm.tech.bazario.backend.dto.AdStatus;
import org.cyberrealm.tech.bazario.backend.dto.PatchAd;
import org.cyberrealm.tech.bazario.backend.dto.ad.CreateAdRequestDto;
import org.cyberrealm.tech.bazario.backend.exception.custom.AuthenticationException;
import org.cyberrealm.tech.bazario.backend.exception.custom.EntityNotFoundException;
import org.cyberrealm.tech.bazario.backend.exception.custom.ForbiddenException;
import org.cyberrealm.tech.bazario.backend.mapper.AdMapper;
import org.cyberrealm.tech.bazario.backend.model.Ad;
import org.cyberrealm.tech.bazario.backend.model.User;
import org.cyberrealm.tech.bazario.backend.model.enums.Role;
import org.cyberrealm.tech.bazario.backend.repository.AdRepository;
import org.cyberrealm.tech.bazario.backend.service.AdService;
import org.cyberrealm.tech.bazario.backend.service.ImageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdServiceImpl implements AdService {
    private final AdRepository adRepository;
    private final AdMapper adMapper;
    private final ImageService imageService;
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public List<AdDto> findPopular(Pageable pageable) {
        Page<Ad> adsPage = adRepository.findAll(pageable);
        return adsPage.stream()
                .map(adMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public AdDto save(CreateAdRequestDto requestDto) {
        return null;
    }

    @Override
    public void save(Ad ad) {
        adRepository.save(ad);
    }

    @Override
    public List<AdDto> findAll(Pageable pageable) {
        return List.of();
    }

    @Override
    public AdDto findById(Long id) {
        return adMapper.toDto(getAd(id));
    }

    @Override
    public AdDto updateById(Long id, CreateAdRequestDto requestDto) {
        return null;
    }

    @Override
    public void deleteById(Long id) {
        var ad = adRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Ad with id " + id + "not found"));
        if (isNotAccessAd(ad)) {
            throw new ForbiddenException("The user not access to ad");
        }
        ad.getImages().forEach(urlImage -> imageService.deleteFile(URI.create(urlImage)));
        adRepository.deleteById(id);
    }

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
    public void patchById(Long id, PatchAd patchAd) {
        var ad = adRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Ad with id " + id + "not found"));
        if (isNotAccessAd(ad)) {
            throw new ForbiddenException("The user not access to ad");
        }
        if (ad.getStatus().equals(AdStatus.DELETE)) {
            ad.getImages().forEach(urlImage ->
                    imageService.deleteFile(URI.create(urlImage)));
        }
        adMapper.updateAdFromDto(patchAd, ad);
        adRepository.save(ad);
    }

    @Override
    public AdDto createOrGet() {
        Ad ad = adRepository.findByStatus(AdStatus.NEW).orElseGet(() ->
                adRepository.save(new Ad()));
        return adMapper.toDto(ad);
    }

    private boolean isNotAccessAd(Ad ad) {
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

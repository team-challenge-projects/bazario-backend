package org.cyberrealm.tech.bazario.backend.service.impl;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.dto.AdResponseDto;
import org.cyberrealm.tech.bazario.backend.dto.AdStatus;
import org.cyberrealm.tech.bazario.backend.exception.custom.EntityNotFoundException;
import org.cyberrealm.tech.bazario.backend.exception.custom.ForbiddenException;
import org.cyberrealm.tech.bazario.backend.mapper.AdMapper;
import org.cyberrealm.tech.bazario.backend.model.Favorite;
import org.cyberrealm.tech.bazario.backend.repository.AdRepository;
import org.cyberrealm.tech.bazario.backend.repository.FavoriteRepository;
import org.cyberrealm.tech.bazario.backend.service.AuthenticationUserService;
import org.cyberrealm.tech.bazario.backend.service.FavoriteService;
import org.cyberrealm.tech.bazario.backend.service.PageableService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final AdRepository adRepository;
    private final AuthenticationUserService authService;
    private final PageableService pageableService;
    private final AdMapper adMapper;

    @Override
    public void add(Long adId) {
        var ad = adRepository.findById(adId).orElseThrow(() ->
                new EntityNotFoundException("Ad with id %d is not found"
                        .formatted(adId)));
        if (!ad.getStatus().equals(AdStatus.ACTIVE)) {
            throw new ForbiddenException("Ad status is not Active by ad with id %d"
                    .formatted(adId));
        }
        var user = authService.getCurrentUser();

        var favorite = new Favorite();
        favorite.setAd(ad);
        favorite.setUser(user);

        favoriteRepository.save(favorite);
    }

    @Override
    public void delete(Long adId) {
        var user = authService.getCurrentUser();
        var favorite = favoriteRepository.findByUser_IdAndAd_Id(user.getId(), adId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Favorite by user with id %d and ad with id %d not found"
                                .formatted(user.getId(), adId)));
        favoriteRepository.delete(favorite);
    }

    @Override
    public Page<AdResponseDto> getAll(Map<String, String> filters) {
        var pageable = pageableService.get(filters);
        var user = authService.getCurrentUser();
        return favoriteRepository.findByUser_Id(user.getId(), pageable)
                .map(favorite -> adMapper.toResponseDto(favorite.getAd()));
    }
}

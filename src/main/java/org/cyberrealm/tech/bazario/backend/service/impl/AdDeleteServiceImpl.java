package org.cyberrealm.tech.bazario.backend.service.impl;

import java.net.URI;
import java.util.Collection;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.dto.AdStatus;
import org.cyberrealm.tech.bazario.backend.model.Ad;
import org.cyberrealm.tech.bazario.backend.model.User;
import org.cyberrealm.tech.bazario.backend.repository.AdRepository;
import org.cyberrealm.tech.bazario.backend.repository.FavoriteRepository;
import org.cyberrealm.tech.bazario.backend.service.AdDeleteService;
import org.cyberrealm.tech.bazario.backend.service.ImageService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdDeleteServiceImpl implements AdDeleteService {
    private final AdRepository adRepository;
    private final ImageService imageService;
    private final FavoriteRepository favoriteRepository;

    @Override
    public void changeStatusByUser(User user, AdStatus status) {
        var ads = adRepository.findByUser(user);
        if (!ads.isEmpty()) {
            if (status.equals(AdStatus.DELETE)) {
                ads.stream().map(Ad::getImages).flatMap(Collection::stream)
                        .forEach(urlImage -> imageService.deleteFile(URI.create(urlImage)));
                ads.forEach(ad -> ad.setImages(Set.of()));
                favoriteRepository.deleteAllByAd_IdIn(ads.stream().map(Ad::getId).toList());
            }
            adRepository.saveAll(ads.stream().peek(ad ->
                    ad.setStatus(status)).toList());
        }
    }

    @Override
    public void deleteByUser(User user) {
        var ads = adRepository.findByUser(user);
        ads.stream().map(Ad::getImages).flatMap(Collection::stream)
                .forEach(urlImage -> imageService.deleteFile(URI.create(urlImage)));
        favoriteRepository.deleteAllByAd_IdIn(ads.stream().map(Ad::getId).toList());
        adRepository.deleteAll(ads);
    }
}

package org.cyberrealm.tech.bazario.backend.service.impl;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.dto.AdDto;
import org.cyberrealm.tech.bazario.backend.dto.AdStatus;
import org.cyberrealm.tech.bazario.backend.dto.PatchAd;
import org.cyberrealm.tech.bazario.backend.dto.ad.CreateAdRequestDto;
import org.cyberrealm.tech.bazario.backend.exception.custom.EntityNotFoundException;
import org.cyberrealm.tech.bazario.backend.exception.custom.ForbiddenException;
import org.cyberrealm.tech.bazario.backend.mapper.AdMapper;
import org.cyberrealm.tech.bazario.backend.model.Ad;
import org.cyberrealm.tech.bazario.backend.model.User;
import org.cyberrealm.tech.bazario.backend.repository.AdRepository;
import org.cyberrealm.tech.bazario.backend.repository.CategoryRepository;
import org.cyberrealm.tech.bazario.backend.service.AccessAdService;
import org.cyberrealm.tech.bazario.backend.service.AdService;
import org.cyberrealm.tech.bazario.backend.service.ImageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdServiceImpl implements AdService {
    private final AdRepository adRepository;
    private final AdMapper adMapper;
    private final AccessAdService accessAdService;
    private final ImageService imageService;
    private final CategoryRepository categoryRepository;

    @Value("${image.min-num}")
    private int minNumImages;

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
    public List<AdDto> findAll(Pageable pageable) {
        return List.of();
    }

    @Override
    public AdDto findById(Long id) {
        return adMapper.toDto(accessAdService.getAd(id));
    }

    @Override
    public AdDto updateById(Long id, CreateAdRequestDto requestDto) {
        return null;
    }

    @Override
    public void deleteById(Long id) {
        var ad = adRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Ad with id " + id + "not found"));
        if (accessAdService.isNotAccessAd(ad)) {
            throw new ForbiddenException("The user not access to ad");
        }
        ad.getImages().forEach(urlImage -> imageService.deleteFile(URI.create(urlImage)));
        adRepository.deleteById(id);
    }

    @Override
    public void patchById(Long id, PatchAd patchAd) {
        var ad = adRepository.findByIdWithParameters(id).orElseThrow(() ->
                new EntityNotFoundException("Ad with id " + id + "not found"));
        if (accessAdService.isNotAccessAd(ad)) {
            throw new ForbiddenException("The user not access to ad");
        }
        if (ad.getStatus().equals(AdStatus.DELETE)) {
            ad.getImages().forEach(urlImage ->
                    imageService.deleteFile(URI.create(urlImage)));
        }

        adMapper.updateAdFromDto(patchAd, ad);

        Long categoryId = patchAd.getCategoryId();
        if (categoryId != null && !ad.getCategory().getId().equals(categoryId)) {
            ad.setCategory(categoryRepository.findById(categoryId).orElseThrow(() ->
                    new EntityNotFoundException("Not category with id " + id)));
        }
        if (ad.getStatus().equals(AdStatus.ACTIVE)
                && ad.getImages().size() < minNumImages) {
            throw new ForbiddenException("Minimum size of images is " + minNumImages);
        }
        adRepository.save(ad);
    }

    @Override
    public AdDto createOrGet() {
        User user = accessAdService.getUser();
        Ad ad = adRepository.findByStatusAndUser(AdStatus.NEW, user).orElseGet(() -> {
            Ad newAd = new Ad();
            newAd.setUser(user);
            newAd.setCategory(categoryRepository.findAll().stream().findFirst()
                    .orElseThrow());
            return adRepository.save(newAd);
        });
        return adMapper.toDto(ad);
    }
}

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
import org.cyberrealm.tech.bazario.backend.repository.AdRepository;
import org.cyberrealm.tech.bazario.backend.service.AccessAdService;
import org.cyberrealm.tech.bazario.backend.service.AdService;
import org.cyberrealm.tech.bazario.backend.service.ImageService;
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
        var ad = adRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Ad with id " + id + "not found"));
        if (accessAdService.isNotAccessAd(ad)) {
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
}

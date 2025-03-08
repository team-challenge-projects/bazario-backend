package org.cyberrealm.tech.bazario.backend.service.impl;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.dto.ad.AdDto;
import org.cyberrealm.tech.bazario.backend.dto.ad.CreateAdRequestDto;
import org.cyberrealm.tech.bazario.backend.mapper.AdMapper;
import org.cyberrealm.tech.bazario.backend.model.Ad;
import org.cyberrealm.tech.bazario.backend.repository.AdRepository;
import org.cyberrealm.tech.bazario.backend.service.AdService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdServiceImpl implements AdService {
    private final AdRepository adRepository;
    private final AdMapper adMapper;

    @Override
    public List<AdDto> findPopular(Pageable pageable) {
        Page<Ad> adsPage = adRepository.findAll(pageable);
        return adsPage.stream()
                .sorted(Comparator.comparingInt((Ad ad) -> ad.getRating()
                                + ad.getViewCount()
                                + ad.getFavoritesCount()
                                + ad.getReviewCount())
                        .reversed())
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
        return null;
    }

    @Override
    public AdDto updateById(Long id, CreateAdRequestDto requestDto) {
        return null;
    }

    @Override
    public void deleteById(Long id) {

    }
}

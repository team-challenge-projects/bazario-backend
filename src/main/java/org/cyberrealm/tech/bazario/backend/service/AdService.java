package org.cyberrealm.tech.bazario.backend.service;

import java.util.List;
import java.util.Map;

import org.cyberrealm.tech.bazario.backend.dto.AdDto;
import org.cyberrealm.tech.bazario.backend.dto.AdResponseDto;
import org.cyberrealm.tech.bazario.backend.dto.PatchAd;
import org.cyberrealm.tech.bazario.backend.dto.ad.CreateAdRequestDto;
import org.cyberrealm.tech.bazario.backend.model.Ad;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdService {
    List<AdDto> findPopular(Pageable pageable);

    AdDto save(CreateAdRequestDto requestDto);

    void save(Ad ad);

    List<AdDto> findAll(Pageable pageable);

    AdDto findById(Long id);

    AdDto updateById(Long id, CreateAdRequestDto requestDto);

    void deleteById(Long id);

    Ad getAd(Long id);

    AdDto createOrGet();

    void patchById(Long id, PatchAd patchAd);

    Page<AdResponseDto> findAll(Map<String, String> filters);
}

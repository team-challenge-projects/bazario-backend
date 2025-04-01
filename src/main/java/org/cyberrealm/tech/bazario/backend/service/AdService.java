package org.cyberrealm.tech.bazario.backend.service;

import java.util.List;
import org.cyberrealm.tech.bazario.backend.dto.ad.AdDto;
import org.cyberrealm.tech.bazario.backend.dto.ad.CreateAdRequestDto;
import org.cyberrealm.tech.bazario.backend.model.Ad;
import org.springframework.data.domain.Pageable;

public interface AdService {
    List<AdDto> findPopular(Pageable pageable);

    AdDto save(CreateAdRequestDto requestDto);

    List<AdDto> findAll(Pageable pageable);

    AdDto findById(Long id);

    AdDto updateById(Long id, CreateAdRequestDto requestDto);

    void deleteById(Long id);

    Ad getAd(Long id);

    void save(Ad ad);
}

package org.cyberrealm.tech.bazario.backend.service;

import java.util.List;
import java.util.Map;
import org.cyberrealm.tech.bazario.backend.dto.AdDto;
import org.cyberrealm.tech.bazario.backend.dto.AdLeaderBoardDto;
import org.cyberrealm.tech.bazario.backend.dto.PatchAd;
import org.cyberrealm.tech.bazario.backend.dto.ad.CreateAdRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdService {
    List<AdDto> findPopular(Pageable pageable);

    AdDto save(CreateAdRequestDto requestDto);

    List<AdDto> findAll(Pageable pageable);

    AdDto findById(Long id);

    AdDto updateById(Long id, CreateAdRequestDto requestDto);

    void deleteById(Long id);

    AdDto createOrGet();

    void patchById(Long id, PatchAd patchAd);

    Page<AdLeaderBoardDto> getLeaderBoard(Map<String, String> filters);

}

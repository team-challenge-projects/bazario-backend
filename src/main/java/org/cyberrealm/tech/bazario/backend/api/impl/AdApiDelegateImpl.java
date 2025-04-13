package org.cyberrealm.tech.bazario.backend.api.impl;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.api.AdApiDelegate;
import org.cyberrealm.tech.bazario.backend.dto.AdDto;
import org.cyberrealm.tech.bazario.backend.dto.PatchAd;
import org.cyberrealm.tech.bazario.backend.service.AdService;
import org.cyberrealm.tech.bazario.backend.service.AdsService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdApiDelegateImpl implements AdApiDelegate {
    private final AdService adService;
    private final AdsService adsService;

    @Override
    public ResponseEntity<List<AdDto>> comparesAd(List<Long> ids) {
        return AdApiDelegate.super.comparesAd(ids);
    }

    @Override
    public ResponseEntity<AdDto> createOrGetAd() {
        return ResponseEntity.ok(adService.createOrGet());
    }

    @Override
    public ResponseEntity<Void> patchAd(Long id, PatchAd patchAd) {
        adService.patchById(id, patchAd);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> deleteAd(Long id) {
        adService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<AdDto> getAd(Long id) {
        return ResponseEntity.ok(adService.findById(id));
    }

    @Override
    public ResponseEntity<Page> getAds(Map<String, String> filters) {
        return ResponseEntity.ok(adsService.findAll(filters));
    }
}

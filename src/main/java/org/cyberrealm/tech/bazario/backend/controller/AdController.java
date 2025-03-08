package org.cyberrealm.tech.bazario.backend.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.dto.ad.AdDto;
import org.cyberrealm.tech.bazario.backend.dto.ad.CreateAdRequestDto;
import org.cyberrealm.tech.bazario.backend.service.AdService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/ads")
public class AdController {
    private AdService adService;

    @GetMapping("popular")
    public List<AdDto> getPopular(Pageable pageable) {
        return adService.findPopular(pageable);
    }

    @PostMapping
    public AdDto createAd(@RequestBody @Valid CreateAdRequestDto requestDto) {
        return adService.save(requestDto);
    }

    @GetMapping
    public List<AdDto> getAll(Pageable pageable) {
        return adService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public AdDto getById(@PathVariable Long id) {
        return adService.findById(id);
    }

    @PostMapping("/{id}")
    public AdDto updateAdById(@PathVariable Long id, @RequestBody CreateAdRequestDto requestDto) {
        return adService.updateById(id, requestDto);
    }

    @DeleteMapping("/{id}")
    public void deleteAd(@PathVariable Long id) {
        adService.deleteById(id);
    }
}

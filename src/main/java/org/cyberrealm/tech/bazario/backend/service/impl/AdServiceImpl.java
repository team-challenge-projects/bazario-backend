package org.cyberrealm.tech.bazario.backend.service.impl;

import java.net.URI;
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.dto.AdDto;
import org.cyberrealm.tech.bazario.backend.dto.AdLeaderBoardDto;
import org.cyberrealm.tech.bazario.backend.dto.AdStatus;
import org.cyberrealm.tech.bazario.backend.dto.PatchAd;
import org.cyberrealm.tech.bazario.backend.exception.custom.EntityNotFoundException;
import org.cyberrealm.tech.bazario.backend.exception.custom.ForbiddenException;
import org.cyberrealm.tech.bazario.backend.mapper.AdMapper;
import org.cyberrealm.tech.bazario.backend.model.Ad;
import org.cyberrealm.tech.bazario.backend.model.User;
import org.cyberrealm.tech.bazario.backend.repository.AdRepository;
import org.cyberrealm.tech.bazario.backend.repository.CategoryRepository;
import org.cyberrealm.tech.bazario.backend.repository.FavoriteRepository;
import org.cyberrealm.tech.bazario.backend.service.AccessAdService;
import org.cyberrealm.tech.bazario.backend.service.AdService;
import org.cyberrealm.tech.bazario.backend.service.AuthenticationUserService;
import org.cyberrealm.tech.bazario.backend.service.ImageService;
import org.cyberrealm.tech.bazario.backend.service.TypeAdParameterService;
import org.cyberrealm.tech.bazario.backend.util.GeometryUtil;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdServiceImpl implements AdService {
    private final AdRepository adRepository;
    private final AuthenticationUserService authUserService;
    private final AdMapper adMapper;
    private final AccessAdService accessAdService;
    private final ImageService imageService;
    private final CategoryRepository categoryRepository;
    private final TypeAdParameterService typeAdParameterService;
    private final FavoriteRepository favoriteRepository;

    @Value("${image.min-num}")
    private int minNumImages;

    @Value("${ad.capacity.disable}")
    private long capacityAdDisable;

    @Override
    public AdDto findById(Long id) {
        Ad ad = accessAdService.getPublicAd(id);
        Point startPoint = null;
        if (authUserService.isAuthenticationUser()) {
            startPoint = authUserService.getCurrentUser().getCityCoordinate();
        }
        return adMapper.toDto(ad, GeometryUtil.haversine(startPoint,
                ad.getCityCoordinate()));
    }

    @Override
    public void deleteById(Long id) {
        var ad = adRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Ad with id " + id + "not found"));
        ad.getImages().forEach(urlImage -> imageService.deleteFile(URI.create(urlImage)));
        favoriteRepository.deleteByAd(ad);
        adRepository.deleteById(id);
    }

    @Override
    public void patchById(Long id, PatchAd patchAd) {

        typeAdParameterService.checkParameters(patchAd.getAdParameters());
        var ad = adRepository.findByIdWithParameters(id).orElseThrow(() ->
                new EntityNotFoundException("Ad with id " + id + "not found"));
        if (accessAdService.isNotAccessAd(ad)) {
            throw new ForbiddenException("The user not access to ad");
        }
        checkStatus(patchAd, ad);

        adMapper.updateAdFromDto(patchAd, ad);
        Optional.ofNullable(patchAd.getCityCoordinate()).ifPresent(coordinate ->
                ad.setCityCoordinate(GeometryUtil.createPoint(coordinate)));

        Optional.ofNullable(patchAd.getPublicationDate()).ifPresent(
                date -> ad.setPublicationDate(authUserService.isAdmin()
                        ? date : LocalDate.now()));

        Long categoryId = patchAd.getCategoryId();
        if (categoryId != null && !ad.getCategory().getId().equals(categoryId)) {
            ad.setCategory(categoryRepository.findById(categoryId).orElseThrow(() ->
                    new EntityNotFoundException("Not category with id " + id)));
        }
        adRepository.save(ad);
    }

    private void checkStatus(PatchAd patchAd, Ad ad) {
        if (patchAd.getStatus() != null && !ad.getStatus().equals(patchAd.getStatus())) {
            ad.setPublicationDate(LocalDate.now());
        }
        switch (patchAd.getStatus()) {
            case NEW -> throw new ForbiddenException("Ad is not change status to NEW");
            case ACTIVE -> {
                if (ad.getImages().size() < minNumImages) {
                    throw new ForbiddenException("Minimum size of images is " + minNumImages);
                }

            }
            case DISABLE -> {
                if (!ad.getStatus().equals(AdStatus.DISABLE)) {
                    Specification<Ad> spec = ((root, query, cb) ->
                            cb.and(cb.equal(root.get("status"), AdStatus.DISABLE),
                                    cb.equal(root.get("user").get("id"), ad.getUser().getId())
                            ));
                    if (adRepository.count(spec) >= capacityAdDisable) {
                        var adMinDate = adRepository.findAll(spec, PageRequest.of(0, 1,
                                Sort.by("publicationDate").ascending())).getContent().get(0);
                        adRepository.delete(adMinDate);
                    }
                }
            }
            case DELETE -> {
                ad.getImages().forEach(urlImage ->
                        imageService.deleteFile(URI.create(urlImage)));
                favoriteRepository.deleteByAd(ad);
                ad.setImages(Set.of());
            }
            default -> { }
        }
    }

    @Override
    public Page<AdLeaderBoardDto> getLeaderBoard(Map<String, String> filters) {
        var size = Integer.parseInt(Optional.of(filters.get("size")).orElse("16"));
        var page = Integer.parseInt(Optional.of(filters.get("page")).orElse("0"));

        var content = accessAdService.getLeaderBoardContent(filters).stream()
                .map(tuple -> new AdLeaderBoardDto()
                        .id((Long) tuple.getValue()).score(Objects
                                .requireNonNull(tuple.getScore()).longValue())).toList();
        var ads = adRepository.findAllById(content.stream().map(AdLeaderBoardDto::getId)
                .toList());
        content.forEach(dto -> {
            ads.stream().filter(e -> e.getId().equals(dto.getId()))
                    .findFirst().ifPresent(e -> {
                        dto.category(e.getCategory().getId())
                                .price(e.getPrice()).description(e.getDescription())
                                .title(e.getTitle()).imageUrl(URI.create(e.getImages()
                                        .iterator().next()));
                    });
        });
        return new PageImpl<>(content, PageRequest.of(page, size),
                accessAdService.getLeaderBoardCount(filters));
    }

    @Override
    public AdDto createOrGet() {
        User user = authUserService.getCurrentUser();
        Ad ad = adRepository.findByStatusAndUser(AdStatus.NEW, user).stream()
                .findFirst().orElseGet(() -> {
                    Ad newAd = new Ad();
                    newAd.setUser(user);
                    newAd.setCategory(categoryRepository.findAll().stream().findFirst()
                            .orElseThrow());
                    return adRepository.save(newAd);
                });
        return adMapper.toDto(ad, GeometryUtil.haversine(user.getCityCoordinate(),
                ad.getCityCoordinate()));
    }
}

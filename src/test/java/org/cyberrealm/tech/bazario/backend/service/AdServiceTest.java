package org.cyberrealm.tech.bazario.backend.service;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.cyberrealm.tech.bazario.backend.dto.AdStatus;
import org.cyberrealm.tech.bazario.backend.mapper.AdMapper;
import org.cyberrealm.tech.bazario.backend.model.Ad;
import org.cyberrealm.tech.bazario.backend.model.Category;
import org.cyberrealm.tech.bazario.backend.model.User;
import org.cyberrealm.tech.bazario.backend.repository.AdRepository;
import org.cyberrealm.tech.bazario.backend.repository.CategoryRepository;
import org.cyberrealm.tech.bazario.backend.repository.FavoriteRepository;
import org.cyberrealm.tech.bazario.backend.service.impl.AdServiceImpl;
import org.cyberrealm.tech.bazario.backend.util.GeometryUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class AdServiceTest {
    @Mock
    private AdRepository adRepository;
    @Mock
    private AuthenticationUserService authUserService;
    @Mock
    private AdMapper adMapper;
    @Mock
    private AccessAdService accessAdService;
    @Mock
    private ImageService imageService;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private TypeAdParameterService typeAdParameterService;
    @Mock
    private FavoriteRepository favoriteRepository;
    @InjectMocks
    private AdServiceImpl adService;
    private String defaultWkt = "POINT(30.3125 50.27)";

    @Test
    void findById() {
        ReflectionTestUtils.setField(adService, "defaultWkt", defaultWkt);
        var user = new User();
        var ad = new Ad();
        when(authUserService.isAuthenticationUser()).thenReturn(true);
        when(authUserService.getCurrentUser()).thenReturn(user);
        when(accessAdService.getPublicAd(1L)).thenReturn(ad);
        adService.findById(1L);

        verify(adMapper).toDto(ad, GeometryUtil.haversine(user.getCityCoordinate(),
                ad.getCityCoordinate(), defaultWkt));
    }

    @Test
    void deleteById() {
        var ad = new Ad();
        ad.setImages(Set.of("adImageOneUrl", "adImageTwoUrl"));

        when(adRepository.findById(1L)).thenReturn(Optional.of(ad));

        adService.deleteById(1L);

        verify(imageService, times(2)).deleteFile(any(URI.class));
        verify(favoriteRepository).deleteByAd(ad);
        verify(adRepository).deleteById(1L);
    }

    @Test
    void createOrGet() {
        ReflectionTestUtils.setField(adService, "defaultWkt", defaultWkt);
        var user = new User();
        var ad = new Ad();
        when(authUserService.getCurrentUser()).thenReturn(user);
        when(adRepository.findByStatusAndUser(AdStatus.NEW, user)).thenReturn(List.of());
        when(categoryRepository.findAll()).thenReturn(List.of(new Category()));
        when(adRepository.save(any(Ad.class))).thenReturn(ad);

        adService.createOrGet();

        verify(adMapper).toDto(ad, GeometryUtil.haversine(user.getCityCoordinate(),
                ad.getCityCoordinate(), defaultWkt));
    }

    @Test
    void patchById() {
    }

    @Test
    void getLeaderBoard() {
    }
}

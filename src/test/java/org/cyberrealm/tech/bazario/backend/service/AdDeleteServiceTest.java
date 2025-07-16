package org.cyberrealm.tech.bazario.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.List;
import java.util.Set;
import org.cyberrealm.tech.bazario.backend.dto.AdStatus;
import org.cyberrealm.tech.bazario.backend.model.Ad;
import org.cyberrealm.tech.bazario.backend.model.User;
import org.cyberrealm.tech.bazario.backend.repository.AdRepository;
import org.cyberrealm.tech.bazario.backend.repository.FavoriteRepository;
import org.cyberrealm.tech.bazario.backend.service.impl.AdDeleteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class AdDeleteServiceTest {
    private User user;
    private Ad adOne;
    private Ad adTwo;
    private List<Ad> ads;

    @Mock
    private AdRepository adRepository;
    @Mock
    private ImageService imageService;
    @Mock
    private FavoriteRepository favoriteRepository;
    @InjectMocks
    private AdDeleteServiceImpl adDeleteService;

    @BeforeEach
    void setUp() {
        user = new User();
        adOne = new Ad();
        adOne.setId(1L);
        adOne.setImages(Set.of("adOneUrlOne", "adOneUrlTwo"));
        adTwo = new Ad();
        adTwo.setId(2L);
        adTwo.setImages(Set.of("adTwoUrlOne", "adTwoUrlTwo"));
        ads = List.of(adOne, adTwo);

        when(adRepository.findByUser(user)).thenReturn(ads);
    }

    @Test
    void changeStatusByUser() {
        adDeleteService.changeStatusByUser(user, AdStatus.DELETE);

        verify(imageService, times(4)).deleteFile(any(URI.class));
        verify(favoriteRepository).deleteAllByAd_IdIn(List.of(adOne.getId(), adTwo.getId()));
        verify(adRepository).saveAll(ads);

        assertEquals(AdStatus.DELETE, adOne.getStatus());
        assertEquals(AdStatus.DELETE, adTwo.getStatus());
    }

    @Test
    void deleteByUser() {
        adDeleteService.deleteByUser(user);

        verify(imageService, times(4)).deleteFile(any(URI.class));
        verify(favoriteRepository).deleteAllByAd_IdIn(List.of(adOne.getId(), adTwo.getId()));
        verify(adRepository).deleteAll(ads);
    }
}

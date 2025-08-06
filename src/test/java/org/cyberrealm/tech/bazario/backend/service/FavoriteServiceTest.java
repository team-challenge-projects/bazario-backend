package org.cyberrealm.tech.bazario.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.cyberrealm.tech.bazario.backend.dto.AdResponseDto;
import org.cyberrealm.tech.bazario.backend.dto.AdStatus;
import org.cyberrealm.tech.bazario.backend.mapper.AdMapper;
import org.cyberrealm.tech.bazario.backend.model.Ad;
import org.cyberrealm.tech.bazario.backend.model.Favorite;
import org.cyberrealm.tech.bazario.backend.model.User;
import org.cyberrealm.tech.bazario.backend.repository.AdRepository;
import org.cyberrealm.tech.bazario.backend.repository.FavoriteRepository;
import org.cyberrealm.tech.bazario.backend.service.impl.FavoriteServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class FavoriteServiceTest {
    private static final long ONE_ID = 1L;
    @Mock
    private FavoriteRepository favoriteRepository;
    @Mock
    private AdRepository adRepository;
    @Mock
    private AuthenticationUserService authService;
    @Mock
    private PageableService pageableService;
    @Mock
    private AdMapper adMapper;
    @InjectMocks
    private FavoriteServiceImpl favoriteService;

    @Test
    void add() {
        var ad = new Ad();
        ad.setStatus(AdStatus.ACTIVE);
        when(adRepository.findById(ONE_ID)).thenReturn(Optional.of(ad));
        var user = new User();
        when(authService.getCurrentUser()).thenReturn(user);

        favoriteService.add(ONE_ID);

        var captor = ArgumentCaptor.forClass(Favorite.class);
        verify(favoriteRepository).save(captor.capture());
        Favorite entity = captor.getValue();
        assertEquals(ad, entity.getAd());
        assertEquals(user, entity.getUser());
    }

    @Test
    void delete() {
        var user = new User();
        user.setId(ONE_ID);
        when(authService.getCurrentUser()).thenReturn(user);
        var favorite = new Favorite();
        when(favoriteRepository.findByUser_IdAndAd_Id(ONE_ID, ONE_ID))
                .thenReturn(Optional.of(favorite));

        favoriteService.delete(ONE_ID);

        verify(favoriteRepository).delete(favorite);
    }

    @Test
    void getAll() {
        var pageable = PageRequest.of(0, 16);
        when(pageableService.get(Map.of())).thenReturn(pageable);
        var user = new User();
        user.setId(ONE_ID);
        when(authService.getCurrentUser()).thenReturn(user);
        var ad = new Ad();
        var favorite = new Favorite();
        favorite.setAd(ad);
        var page = new PageImpl<>(List.of(favorite),pageable, ONE_ID);
        when(favoriteRepository.findByUser_Id(ONE_ID, pageable)).thenReturn(page);
        var adResponse = new AdResponseDto();
        when(adMapper.toResponseDto(ad)).thenReturn(adResponse);

        var pageResponse = favoriteService.getAll(Map.of());

        assertEquals(pageable, pageResponse.getPageable());
        assertEquals(adResponse, pageResponse.getContent().get(0));
        assertEquals(ONE_ID, pageResponse.getTotalElements());
    }
}

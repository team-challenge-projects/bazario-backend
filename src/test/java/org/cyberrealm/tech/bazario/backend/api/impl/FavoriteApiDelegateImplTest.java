package org.cyberrealm.tech.bazario.backend.api.impl;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import org.cyberrealm.tech.bazario.backend.AbstractIntegrationTest;
import org.cyberrealm.tech.bazario.backend.dto.AdResponseDto;
import org.cyberrealm.tech.bazario.backend.repository.AdRepository;
import org.cyberrealm.tech.bazario.backend.repository.FavoriteRepository;
import org.cyberrealm.tech.bazario.backend.repository.UserRepository;
import org.cyberrealm.tech.bazario.backend.service.AuthenticationUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

class FavoriteApiDelegateImplTest extends AbstractIntegrationTest {
    @MockitoBean
    private AuthenticationUserService authService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AdRepository adRepository;
    @Autowired
    private FavoriteRepository favoriteRepository;

    @WithMockUser
    @Test
    void addFavorite() throws Exception {
        var user = userRepository.findById(ID_TWO).orElseThrow();
        when(authService.getCurrentUser()).thenReturn(user);
        mockMvc.perform(post("/private/favorite/" + ID_ONE))
                .andExpect(status().isNoContent());
        assertTrue(favoriteRepository.findByUser_IdAndAd_Id(ID_TWO, ID_ONE).isPresent());
    }

    @WithMockUser
    @Test
    void deleteFavorite() throws Exception {
        var user = userRepository.findById(ID_ONE).orElseThrow();
        when(authService.getCurrentUser()).thenReturn(user);

        mockMvc.perform(delete("/private/favorite/" + ID_ONE))
                .andExpect(status().isNoContent());
    }

    @WithMockUser
    @Test
    void getFavorite() throws Exception {
        var user = userRepository.findById(ID_ONE).orElseThrow();
        when(authService.getCurrentUser()).thenReturn(user);

        var dto = new AdResponseDto().id(ID_ONE).title("Тест")
                .description("Тест").price(BigDecimal.valueOf(1000.00))
                .category(ID_ONE).cityName("Kiev")
                .imageUrl(URI.create("http://test/test.png"));
        var pageDto = new PageImpl<AdResponseDto>(List.of(dto),
                PageRequest.of(0, 16,
                        Sort.by("id").ascending()), 1);
        mockMvc.perform(get("/private/favorites"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(pageDto)));
    }
}

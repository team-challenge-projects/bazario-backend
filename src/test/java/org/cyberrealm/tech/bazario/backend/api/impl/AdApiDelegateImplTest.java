package org.cyberrealm.tech.bazario.backend.api.impl;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import lombok.SneakyThrows;
import org.cyberrealm.tech.bazario.backend.AbstractIntegrationTest;
import org.cyberrealm.tech.bazario.backend.dto.AdDto;
import org.cyberrealm.tech.bazario.backend.dto.AdResponseDto;
import org.cyberrealm.tech.bazario.backend.dto.AdStatus;
import org.cyberrealm.tech.bazario.backend.dto.BasicUserParameter;
import org.cyberrealm.tech.bazario.backend.dto.PatchAd;
import org.cyberrealm.tech.bazario.backend.repository.AdRepository;
import org.cyberrealm.tech.bazario.backend.repository.UserRepository;
import org.cyberrealm.tech.bazario.backend.service.AuthenticationUserService;
import org.cyberrealm.tech.bazario.backend.service.ImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

class AdApiDelegateImplTest extends AbstractIntegrationTest {
    @MockitoBean
    private AuthenticationUserService authUserService;
    @MockitoBean
    private ImageService imageService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AdRepository adRepository;

    @BeforeEach
    void setUp() {
        var user = userRepository.findById(ID_ONE).orElseThrow();
        when(authUserService.getCurrentUser()).thenReturn(user);
        when(authUserService.isAuthenticationUser()).thenReturn(true);
    }

    @Test
    void comparesAd() {
    }

    @SneakyThrows
    @WithMockUser
    @Test
    void createOrGetAd() {
        var dto = new AdDto().id(ID_TWO).title("").description("")
                .price(BigDecimal.ZERO);
        mockMvc.perform(post("/private/ad"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
        var newEntity = adRepository.findByIdWithParameters(ID_TWO).orElseThrow();
        assertAll(
                () -> assertEquals(dto.getPrice(), newEntity.getPrice()),
                () -> assertEquals(AdStatus.NEW, newEntity.getStatus()),
                () -> assertEquals(dto.getTitle(), newEntity.getTitle()),
                () -> assertEquals(dto.getDescription(), newEntity.getDescription()),
                () -> assertEquals(Set.of(), newEntity.getParameters())
        );
    }

    @SneakyThrows
    @WithMockUser
    @Test
    void patchAd() {
        String newValue = "Новий Тест";
        var dto = new PatchAd().title(newValue).description(newValue)
                .status(AdStatus.ACTIVE).price(BigDecimal.valueOf(2000.00))
                .categoryId(ID_ONE).addAdParametersItem(new BasicUserParameter()
                        .id(ID_ONE).parameterId(ID_ONE).parameterValue("ТестПошта")
                        .name("Доставка тест"));

        var oldEntity = adRepository.findByIdWithParameters(ID_ONE).orElseThrow();
        entityManager.clear();

        mockMvc.perform(patch("/private/ad/" + ID_ONE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());
        var newEntity = adRepository.findByIdWithParameters(ID_ONE).orElseThrow();
        assertAll(
                () -> assertEquals(oldEntity.getId(), newEntity.getId(),
                        "Ids entities is not equals"),
                () -> assertNotEquals(oldEntity.getTitle(), newEntity.getTitle(),
                        "Title is equals"),
                () -> assertEquals(newValue, newEntity.getTitle(),
                        "Title is not equals new value"),
                () -> assertNotEquals(oldEntity.getPrice(), newEntity.getPrice()),
                () -> assertEquals(oldEntity.getStatus(), newEntity.getStatus(),
                        "Status is not equals")
        );
    }

    @SneakyThrows
    @WithMockUser(roles = {"ADMIN"})
    @Test
    void deleteAd() {
        mockMvc.perform(delete("/admin/ad/" + ID_ONE))
                .andExpect(status().isNoContent());
        assertThrows(NoSuchElementException.class, () ->
                adRepository.findById(ID_ONE).orElseThrow());
    }

    @SneakyThrows
    @Test
    void getAd() {
        var dto = new AdDto().id(ID_ONE).title("Тест")
                .description("Тест").price(BigDecimal.valueOf(1000.00))
                .images(List.of(URI.create("http://test/test.png"),
                        URI.create("http://test/old-test.png")))
                .addAdParametersItem(new BasicUserParameter()
                        .id(ID_ONE).parameterValue("ТестПошта")
                        .parameterId(ID_ONE).name("Доставка тест"));
        mockMvc.perform(get("/public/ad/" + ID_ONE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }

    @SneakyThrows
    @Test
    void getAds() {
        var contentDto = new AdResponseDto().id(ID_ONE).title("Тест")
                .description("Тест").price(BigDecimal.valueOf(1000.00))
                .imageUrl(URI.create("http://test/test.png"))
                .category(ID_ONE);
        var dto = new PageImpl<AdResponseDto>(List.of(contentDto),
                PageRequest.of(0,16, Sort.by("id").ascending()), 1L);
        mockMvc.perform(get("/public/ads")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of())))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }
}

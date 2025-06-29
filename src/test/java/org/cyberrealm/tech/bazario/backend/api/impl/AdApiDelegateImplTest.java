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
import java.time.Duration;
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
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

class AdApiDelegateImplTest extends AbstractIntegrationTest {
    private static final long ID_FOUR = 4L;

    @MockitoBean
    private AuthenticationUserService authUserService;
    @MockitoBean
    private ImageService imageService;
    @MockitoBean
    private ZSetOperations<String, Object> opsForZSet;
    @MockitoBean
    private ValueOperations<String, Object> opsForValue;
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
        var dto = new AdDto().id(ID_FOUR).title("").description("")
                .price(BigDecimal.ZERO);
        mockMvc.perform(post("/private/ad"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
        var newEntity = adRepository.findByIdWithParameters(ID_FOUR).orElseThrow();
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
                        .id(ID_ONE).typeId(ID_ONE).parameterValue("ТестПошта")
                        .typeName("Доставка тест"));

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
        var ad = adRepository.findById(ID_ONE).orElseThrow();

        when(redisTemplate.opsForZSet()).thenReturn(opsForZSet);
        when(redisTemplate.opsForValue()).thenReturn(opsForValue);
        when(opsForZSet.addIfAbsent(ArgumentMatchers.anyString(),
                ArgumentMatchers.anyLong(), ArgumentMatchers.anyDouble()))
                .thenReturn(true);
        when(opsForZSet.incrementScore(ArgumentMatchers.anyString(),
                ArgumentMatchers.anyLong(), ArgumentMatchers.anyDouble()))
                .thenReturn(1.0);
        when(opsForValue.setIfAbsent(ArgumentMatchers.anyString(),
                ArgumentMatchers.anyLong(), ArgumentMatchers.any(Duration.class)))
                .thenReturn(true);
        when(opsForZSet.size(ArgumentMatchers.anyString())).thenReturn(1L);

        var dto = new AdDto().id(ID_ONE).title("Тест")
                .description("Тест").price(BigDecimal.valueOf(1000.00))
                .images(List.of(URI.create("http://test/test.png"),
                        URI.create("http://test/old-test.png")))
                .addAdParametersItem(new BasicUserParameter()
                        .id(ID_ONE).parameterValue("ТестПошта")
                        .typeId(ID_ONE).typeName("Доставка тест"));
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
        var contentDtoTwo = new AdResponseDto().id(ID_TWO).title("Тест search text")
                .description("Тест search text").price(BigDecimal.valueOf(3500.00))
                .imageUrl(URI.create(""))
                .category(ID_TWO);
        var contentDtoThree = new AdResponseDto().id(3L).title("Тест")
                .description("Тест").price(BigDecimal.valueOf(5000.00))
                .imageUrl(URI.create(""))
                .category(ID_ONE);
        var dto = new PageImpl<AdResponseDto>(List.of(contentDto, contentDtoTwo, contentDtoThree),
                PageRequest.of(0,16, Sort.by("id").ascending()), 1L);
        mockMvc.perform(get("/public/ads")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of())))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }
}

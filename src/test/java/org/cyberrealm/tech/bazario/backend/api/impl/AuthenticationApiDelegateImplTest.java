package org.cyberrealm.tech.bazario.backend.api.impl;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.servlet.http.Cookie;
import java.time.Duration;
import lombok.SneakyThrows;
import org.cyberrealm.tech.bazario.backend.AbstractIntegrationTest;
import org.cyberrealm.tech.bazario.backend.dto.AuthenticationRequest;
import org.cyberrealm.tech.bazario.backend.model.RefreshToken;
import org.cyberrealm.tech.bazario.backend.model.User;
import org.cyberrealm.tech.bazario.backend.repository.RefreshTokenRepository;
import org.cyberrealm.tech.bazario.backend.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

class AuthenticationApiDelegateImplTest extends AbstractIntegrationTest {
    private static final String REFRESH_TOKEN = "refreshToken";
    private static final String ACCESS_TOKEN = "accessToken";
    private static final String EMAIL = "test@test.com";

    @MockitoBean
    private JwtUtil jwtUtil;
    @Autowired
    private RefreshTokenRepository repository;

    @SneakyThrows
    @Test
    void login() {
        var dto = new AuthenticationRequest();
        dto.setEmail(EMAIL);
        dto.setPassword("rooT1#root");

        when(jwtUtil.generateAccessToken(dto.getEmail())).thenReturn(ACCESS_TOKEN);
        when(jwtUtil.generateRefreshToken(dto.getEmail())).thenReturn(REFRESH_TOKEN);

        mockMvc.perform(post("/anonymous/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(cookie().value(REFRESH_TOKEN, REFRESH_TOKEN))
                .andExpect(content().string(ACCESS_TOKEN));

        var entityRefreshToken = repository.findByToken(REFRESH_TOKEN);
        assertAll(
                () -> assertTrue(entityRefreshToken.isPresent()),
                () -> assertEquals(REFRESH_TOKEN, entityRefreshToken.orElseThrow().getToken()),
                () -> assertEquals(ID_ONE, entityRefreshToken.orElseThrow().getUser().getId())
        );
    }

    @SneakyThrows
    @Test
    void refreshToken() {
        var user = new User();
        user.setId(ID_ONE);
        var refreshToken = RefreshToken.builder().user(user)
                .token(REFRESH_TOKEN).build();
        repository.save(refreshToken);
        entityManager.clear();

        when(jwtUtil.isValidToken(REFRESH_TOKEN)).thenReturn(true);
        when(jwtUtil.getUsername(REFRESH_TOKEN)).thenReturn(EMAIL);
        when(jwtUtil.generateAccessToken(EMAIL)).thenReturn(ACCESS_TOKEN);

        mockMvc.perform(post("/public/refreshToken")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(new Cookie(REFRESH_TOKEN, REFRESH_TOKEN)))
                .andExpect(status().isOk())
                .andExpect(content().string(ACCESS_TOKEN));

        var newEntity = repository.findByToken(REFRESH_TOKEN);
        assertAll(
                () -> assertTrue(newEntity.isPresent()),
                () -> assertEquals(REFRESH_TOKEN, newEntity.orElseThrow().getToken()),
                () -> assertEquals(ID_ONE, newEntity.orElseThrow().getUser().getId())
        );
    }

    @SneakyThrows
    @WithMockUser
    @Test
    void logoutToken() {
        var user = new User();
        user.setId(ID_ONE);
        var refreshToken = RefreshToken.builder().user(user)
                .token(REFRESH_TOKEN).build();
        repository.save(refreshToken);
        entityManager.clear();

        mockMvc.perform(post("/private/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(new Cookie(REFRESH_TOKEN, REFRESH_TOKEN)))
                .andExpect(status().isNoContent())
                .andExpect(cookie().value(REFRESH_TOKEN, REFRESH_TOKEN))
                .andExpect(cookie().maxAge(REFRESH_TOKEN, Duration.ZERO.toMillisPart()));

        var newEntity = repository.findByToken(REFRESH_TOKEN);
        assertTrue(newEntity.isEmpty());
    }
}

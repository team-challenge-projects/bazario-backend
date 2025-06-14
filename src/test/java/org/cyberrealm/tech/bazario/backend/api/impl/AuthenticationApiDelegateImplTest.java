package org.cyberrealm.tech.bazario.backend.api.impl;

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
import org.cyberrealm.tech.bazario.backend.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

class AuthenticationApiDelegateImplTest extends AbstractIntegrationTest {
    private static final String REFRESH_TOKEN = "refreshToken";
    private static final String ACCESS_TOKEN = "accessToken";
    private static final String EMAIL = "test@test.com";

    @MockitoBean
    private JwtUtil jwtUtil;

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
    }

    @SneakyThrows
    @Test
    void refreshToken() {
        when(jwtUtil.isValidToken(REFRESH_TOKEN)).thenReturn(true);
        when(jwtUtil.getUsername(REFRESH_TOKEN)).thenReturn(EMAIL);
        when(jwtUtil.generateAccessToken(EMAIL)).thenReturn(ACCESS_TOKEN);

        mockMvc.perform(post("/public/refreshToken")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(new Cookie(REFRESH_TOKEN, REFRESH_TOKEN)))
                .andExpect(status().isOk())
                .andExpect(content().string(ACCESS_TOKEN));
    }

    @SneakyThrows
    @WithMockUser
    @Test
    void logoutToken() {
        mockMvc.perform(post("/private/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(new Cookie(REFRESH_TOKEN, REFRESH_TOKEN)))
                .andExpect(status().isNoContent())
                .andExpect(cookie().value(REFRESH_TOKEN, REFRESH_TOKEN))
                .andExpect(cookie().maxAge(REFRESH_TOKEN, Duration.ZERO.toMillisPart()));
    }
}
